package game.ludora.engine.remix

import game.ludora.core.model.PlayerColor
import game.ludora.core.model.TokenState
import game.ludora.engine.core.DiceRoller
import game.ludora.engine.core.SecureDiceRoller
import game.ludora.engine.ludo.LudoGameEngine
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoToken
import game.ludora.engine.remix.model.PowerUp
import game.ludora.engine.remix.model.PowerUpType
import game.ludora.engine.remix.model.RemixConfig
import kotlinx.serialization.Serializable

@Serializable
data class RemixGameState(
    val baseState: LudoGameState,
    val config: RemixConfig = RemixConfig(),
    val inventories: Map<PlayerColor, List<PowerUpType>> = emptyMap(),
    val shieldedTokens: Set<Pair<PlayerColor, Int>> = emptySet(),
    val activeRollModifier: Int = 0
) {
    val isGameOver: Boolean get() = baseState.isGameOver
    val winners: List<PlayerColor> get() = baseState.winners
    val activePlayerColor: PlayerColor get() = baseState.activePlayer.color
}

/**
 * Remix Mode engine applying dynamic modifiers and tactical power-ups to classic board gameplay.
 */
class RemixGameEngine(
    private val baseEngine: LudoGameEngine = LudoGameEngine(SecureDiceRoller),
    private val diceRoller: DiceRoller = SecureDiceRoller
) {

    fun getInitialState(playerCount: Int, config: RemixConfig = RemixConfig()): RemixGameState {
        val base = baseEngine.getInitialState(playerCount)

        // For quick match, truncate player tokens to tokenCountPerPlayer
        val modifiedPlayers = base.players.map { playerState ->
            val activeTokens = playerState.tokens.take(config.tokenCountPerPlayer)
            playerState.copy(tokens = activeTokens)
        }

        val initialInventories = base.players.associate {
            it.color to listOf(PowerUpType.SHIELD, PowerUpType.SPEED_BOOST)
        }

        return RemixGameState(
            baseState = base.copy(players = modifiedPlayers),
            config = config,
            inventories = initialInventories,
            shieldedTokens = emptySet(),
            activeRollModifier = 0
        )
    }

    fun activatePowerUp(
        state: RemixGameState,
        playerColor: PlayerColor,
        powerUp: PowerUpType,
        targetTokenId: Int? = null
    ): RemixGameState {
        val playerInv = state.inventories[playerColor] ?: emptyList()
        if (!playerInv.contains(powerUp)) return state

        val updatedInv = playerInv.toMutableList()
        updatedInv.remove(powerUp)
        val newInventories = state.inventories + (playerColor to updatedInv)

        return when (powerUp) {
            PowerUpType.SHIELD -> {
                val tokenId = targetTokenId ?: 0
                state.copy(
                    inventories = newInventories,
                    shieldedTokens = state.shieldedTokens + (playerColor to tokenId)
                )
            }
            PowerUpType.SPEED_BOOST -> {
                state.copy(
                    inventories = newInventories,
                    activeRollModifier = state.activeRollModifier + 2
                )
            }
            PowerUpType.SWAP -> {
                // Swaps active token with opponent's forward-most token on the track
                val activePlayer = state.baseState.players.firstOrNull { it.color == playerColor }
                    ?: return state.copy(inventories = newInventories)

                val activeToken = activePlayer.tokens.firstOrNull {
                    it.id == (targetTokenId ?: 0) && it.position is LudoPosition.OnTrack
                } ?: return state.copy(inventories = newInventories)

                val opponentTokens = state.baseState.players
                    .filter { it.color != playerColor }
                    .flatMap { p -> p.tokens.filter { it.position is LudoPosition.OnTrack }.map { p.color to it } }

                val targetOpponent = opponentTokens.maxByOrNull {
                    (it.second.position as LudoPosition.OnTrack).stepIndex
                } ?: return state.copy(inventories = newInventories)

                val oppColor = targetOpponent.first
                val oppToken = targetOpponent.second

                val swappedActivePos = oppToken.position
                val swappedOppPos = activeToken.position

                val newPlayers = state.baseState.players.map { p ->
                    when (p.color) {
                        playerColor -> p.copy(tokens = p.tokens.map {
                            if (it.id == activeToken.id) it.copy(position = swappedActivePos) else it
                        })
                        oppColor -> p.copy(tokens = p.tokens.map {
                            if (it.id == oppToken.id) it.copy(position = swappedOppPos) else it
                        })
                        else -> p
                    }
                }

                state.copy(
                    baseState = state.baseState.copy(players = newPlayers),
                    inventories = newInventories
                )
            }
            PowerUpType.BOMB -> {
                // Sends any opponent token within +/- 2 steps back to base
                val tokenId = targetTokenId ?: 0
                val activePlayer = state.baseState.players.firstOrNull { it.color == playerColor }
                    ?: return state.copy(inventories = newInventories)
                val token = activePlayer.tokens.firstOrNull { it.id == tokenId }
                    ?: return state.copy(inventories = newInventories)

                val centerStep = (token.position as? LudoPosition.OnTrack)?.stepIndex
                    ?: return state.copy(inventories = newInventories)

                val newPlayers = state.baseState.players.map { p ->
                    if (p.color == playerColor) p
                    else p.copy(tokens = p.tokens.map { oppToken ->
                        val oppPos = oppToken.position as? LudoPosition.OnTrack
                        if (oppPos != null && kotlin.math.abs(oppPos.stepIndex - centerStep) <= 2) {
                            oppToken.copy(
                                position = LudoPosition.InBase(oppToken.id),
                                state = TokenState.IN_BASE
                            )
                        } else oppToken
                    })
                }

                state.copy(
                    baseState = state.baseState.copy(players = newPlayers),
                    inventories = newInventories
                )
            }
        }
    }

    fun step(state: RemixGameState, action: LudoAction): RemixGameState {
        val modifiedAction = if (action is LudoAction.RollDice && state.activeRollModifier > 0) {
            val baseRoll = action.forcedValue ?: diceRoller.roll()
            val totalRoll = (baseRoll + state.activeRollModifier).coerceAtMost(6)
            LudoAction.RollDice(forcedValue = totalRoll)
        } else action

        val (nextBase, _) = baseEngine.step(state.baseState, modifiedAction)

        // Clear active roll modifier once dice is rolled and handled
        val clearedModifier = if (action is LudoAction.RollDice) 0 else state.activeRollModifier

        return state.copy(
            baseState = nextBase,
            activeRollModifier = clearedModifier
        )
    }
}
