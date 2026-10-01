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
import game.ludora.engine.ludo.model.LudoTurnPhase
import game.ludora.engine.remix.model.ChaosModifier
import game.ludora.engine.remix.model.HazardType
import game.ludora.engine.remix.model.PowerUp
import game.ludora.engine.remix.model.PowerUpType
import game.ludora.engine.remix.model.RemixConfig
import game.ludora.engine.remix.model.RemixHazard
import kotlinx.serialization.Serializable

@Serializable
data class RemixGameState(
    val baseState: LudoGameState,
    val config: RemixConfig = RemixConfig(),
    val inventories: Map<PlayerColor, List<PowerUpType>> = emptyMap(),
    val shieldedTokens: Set<Pair<PlayerColor, Int>> = emptySet(),
    val activeRollModifier: Int = 0,
    val activeChaosModifier: ChaosModifier = ChaosModifier.NONE,
    val lastTriggeredHazardDescription: String? = null
) {
    val isGameOver: Boolean get() = baseState.isGameOver
    val winners: List<PlayerColor> get() = baseState.winners
    val activePlayerColor: PlayerColor get() = baseState.activePlayer.color
}

/**
 * Remix Mode engine integrating hybrid hazard tiles (Ladders and Snakes on track),
 * a complete power-card system (Shield, Speed Boost, Reroll, Swap, Bomb),
 * and dynamic Chaos Modifiers.
 */
class RemixGameEngine(
    private val baseEngine: LudoGameEngine = LudoGameEngine(SecureDiceRoller),
    private val diceRoller: DiceRoller = SecureDiceRoller
) {

    fun getInitialState(playerCount: Int, config: RemixConfig = RemixConfig()): RemixGameState {
        val base = baseEngine.getInitialState(playerCount)

        val modifiedPlayers = base.players.map { playerState ->
            val activeTokens = playerState.tokens.take(config.tokenCountPerPlayer)
            playerState.copy(tokens = activeTokens)
        }

        val initialInventories = base.players.associate {
            it.color to listOf(
                PowerUpType.SHIELD,
                PowerUpType.SPEED_BOOST,
                PowerUpType.REROLL,
                PowerUpType.SWAP,
                PowerUpType.BOMB
            )
        }

        return RemixGameState(
            baseState = base.copy(players = modifiedPlayers),
            config = config,
            inventories = initialInventories,
            shieldedTokens = emptySet(),
            activeRollModifier = 0,
            activeChaosModifier = if (config.enableChaosModifiers) ChaosModifier.NONE else ChaosModifier.NONE,
            lastTriggeredHazardDescription = null
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
            PowerUpType.REROLL -> {
                // Reroll card resets turn phase to WAITING_FOR_ROLL so the player rolls again
                val resetBase = state.baseState.copy(
                    phase = LudoTurnPhase.WAITING_FOR_ROLL,
                    currentRoll = null
                )
                state.copy(
                    baseState = resetBase,
                    inventories = newInventories,
                    activeRollModifier = 0
                )
            }
            PowerUpType.SWAP -> {
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
                        val isOppShielded = state.shieldedTokens.contains(p.color to oppToken.id)
                        if (oppPos != null && kotlin.math.abs(oppPos.stepIndex - centerStep) <= 2 && !isOppShielded) {
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
        val chaosModifierBonus = if (state.activeChaosModifier == ChaosModifier.DOUBLE_ROLL) 2 else 0
        val totalModifier = state.activeRollModifier + chaosModifierBonus

        val modifiedAction = if (action is LudoAction.RollDice && totalModifier > 0) {
            val baseRoll = action.forcedValue ?: diceRoller.roll()
            val totalRoll = (baseRoll + totalModifier).coerceAtMost(6)
            LudoAction.RollDice(forcedValue = totalRoll)
        } else action

        val (nextBase, _) = baseEngine.step(state.baseState, modifiedAction)

        var finalBase = nextBase
        var finalShields = state.shieldedTokens
        var hazardMsg: String? = null

        // Resolve Hybrid Hazard (Snake or Ladder) on track when moving a token
        if (state.config.enableHazards && action is LudoAction.SelectMove) {
            val movingTokenId = action.tokenId
            val activeColor = state.baseState.activePlayer.color
            val movedToken = finalBase.players.firstOrNull { it.color == activeColor }
                ?.tokens?.firstOrNull { it.id == movingTokenId }

            val trackPos = (movedToken?.position as? LudoPosition.OnTrack)?.stepIndex
            if (trackPos != null) {
                val hazard = RemixHazard.getHazardAt(trackPos)
                if (hazard != null) {
                    val isShielded = state.shieldedTokens.contains(activeColor to movingTokenId)
                    if (isShielded) {
                        // Deflect hazard using shield
                        finalShields = finalShields - (activeColor to movingTokenId)
                        hazardMsg = "Shield deflected hazard at step $trackPos!"
                    } else {
                        // Trigger hazard
                        val newStep = hazard.toStep
                        val updatedPlayers = finalBase.players.map { p ->
                            if (p.color == activeColor) {
                                p.copy(tokens = p.tokens.map { t ->
                                    if (t.id == movingTokenId) t.copy(position = LudoPosition.OnTrack(newStep))
                                    else t
                                })
                            } else p
                        }
                        finalBase = finalBase.copy(players = updatedPlayers)
                        hazardMsg = when (hazard.type) {
                            HazardType.LADDER -> "Ladder leaped from $trackPos to $newStep! 🪜"
                            HazardType.SNAKE -> "Snake swallowed token from $trackPos to $newStep! 🐍"
                        }
                    }
                }
            }
        }

        // Check if round advanced, rotate chaos modifier
        val newChaosModifier = if (state.config.enableChaosModifiers && nextBase.roundCount != state.baseState.roundCount) {
            when (nextBase.roundCount % 4) {
                1 -> ChaosModifier.DOUBLE_ROLL
                2 -> ChaosModifier.HAZARD_RUSH
                3 -> ChaosModifier.SHIELD_FRENZY
                else -> ChaosModifier.NONE
            }
        } else {
            state.activeChaosModifier
        }

        val clearedModifier = if (action is LudoAction.RollDice) 0 else state.activeRollModifier

        return state.copy(
            baseState = finalBase,
            shieldedTokens = finalShields,
            activeRollModifier = clearedModifier,
            activeChaosModifier = newChaosModifier,
            lastTriggeredHazardDescription = hazardMsg
        )
    }
}
