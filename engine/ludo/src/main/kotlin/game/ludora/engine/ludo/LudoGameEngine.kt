package game.ludora.engine.ludo

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import game.ludora.core.model.Token
import game.ludora.core.model.TokenState
import game.ludora.engine.core.DiceRoller
import game.ludora.engine.core.EngineAction
import game.ludora.engine.core.EngineResult
import game.ludora.engine.core.GameEngine
import game.ludora.engine.core.SecureDiceRoller
import game.ludora.engine.ludo.logic.LudoMoveValidator
import game.ludora.engine.ludo.logic.LudoStateReducer
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoBoard
import game.ludora.engine.ludo.model.LudoEvent
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPlayerState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoToken
import game.ludora.engine.ludo.model.LudoTurnPhase

/**
 * Headless, deterministic game engine for Classic Ludo conforming to docs/04_GAME_RULES.md.
 */
class LudoGameEngine(
    private val diceRoller: DiceRoller = SecureDiceRoller
) : GameEngine<LudoGameState, EngineAction> {

    override val rulesetId: String = "LUDO_CLASSIC_V1"

    override fun getInitialState(playerCount: Int, seed: Long): LudoGameState {
        require(playerCount in 2..4) { "Player count must be between 2 and 4, got $playerCount" }

        val colors = when (playerCount) {
            2 -> listOf(PlayerColor.RED, PlayerColor.YELLOW) // Opposing seats
            3 -> listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW)
            4 -> listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE)
            else -> listOf(PlayerColor.RED, PlayerColor.YELLOW)
        }

        val players = colors.map { color ->
            val seatIndex = when (color) {
                PlayerColor.RED -> 0
                PlayerColor.GREEN -> 1
                PlayerColor.YELLOW -> 2
                PlayerColor.BLUE -> 3
            }
            val player = Player(
                id = "player_${color.name.lowercase()}",
                displayName = color.name.lowercase().replaceFirstChar { it.uppercase() },
                seatIndex = seatIndex,
                color = color
            )
            LudoPlayerState.initial(player)
        }

        return LudoGameState(
            matchId = "ludo_${if (seed != 0L) seed else System.currentTimeMillis()}",
            players = players,
            activeSeatIndex = players.first().seatIndex,
            currentRoll = null,
            phase = LudoTurnPhase.WAITING_FOR_ROLL,
            roundCount = 1,
            winners = emptyList(),
            isGameOver = false
        )
    }

    override fun computeLegalMoves(state: LudoGameState, rollValue: Int): List<Token> {
        val legalMoves = LudoMoveValidator.getLegalMoves(state, rollValue)
        val activePlayer = state.activePlayer
        return legalMoves.mapNotNull { move ->
            activePlayer.tokens.firstOrNull { it.id == move.tokenId }?.toCoreToken()
        }
    }

    override fun reduce(
        state: LudoGameState,
        action: EngineAction,
        rollValue: Int?
    ): EngineResult<LudoGameState> {
        val ludoAction = when (action) {
            is EngineAction.RollDice -> LudoAction.RollDice(forcedValue = rollValue)
            is EngineAction.SelectToken -> LudoAction.SelectMove(tokenId = action.tokenId.toIntOrNull() ?: 0)
            is EngineAction.ReleaseToken -> LudoAction.SelectMove(tokenId = action.tokenId.toIntOrNull() ?: 0)
            is EngineAction.Forfeit -> LudoAction.PassTurn
            is EngineAction.Timeout -> LudoAction.PassTurn
        }

        val (nextState, events) = step(state, ludoAction)
        return EngineResult(
            state = nextState,
            events = events,
            isTerminal = nextState.isGameOver
        )
    }

    /**
     * Executes a single transition step with native Ludo actions.
     */
    fun step(
        state: LudoGameState,
        action: LudoAction
    ): Pair<LudoGameState, List<LudoEvent>> {
        return LudoStateReducer.reduce(state, action, diceRoller)
    }

    private fun LudoToken.toCoreToken(): Token {
        val (trackPos, steps) = when (val p = position) {
            is LudoPosition.InBase -> -1 to 0
            is LudoPosition.OnTrack -> p.stepIndex to ((p.stepIndex - LudoBoard.startStep(color) + LudoBoard.TRACK_SIZE) % LudoBoard.TRACK_SIZE)
            is LudoPosition.InHomePath -> p.stepIndex to (50 + p.stepIndex)
            is LudoPosition.Finished -> 56 to 56
        }
        return Token(
            id = "$id",
            color = color,
            tokenIndex = id,
            state = state,
            trackPosition = trackPos,
            stepsMoved = steps
        )
    }
}
