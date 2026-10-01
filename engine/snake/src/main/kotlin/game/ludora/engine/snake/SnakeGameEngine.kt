package game.ludora.engine.snake

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import game.ludora.core.model.Token
import game.ludora.core.model.TokenState
import game.ludora.engine.core.DiceRoller
import game.ludora.engine.core.EngineAction
import game.ludora.engine.core.EngineResult
import game.ludora.engine.core.GameEngine
import game.ludora.engine.core.GameEvent
import game.ludora.engine.core.SecureDiceRoller
import game.ludora.engine.snake.logic.SnakeStateReducer
import game.ludora.engine.snake.model.SnakeAction
import game.ludora.engine.snake.model.SnakeEvent
import game.ludora.engine.snake.model.SnakeGameState
import game.ludora.engine.snake.model.SnakePlayerState
import game.ludora.engine.snake.model.SnakeTurnPhase

/**
 * Deterministic Snake and Ladder engine implementation conforming to docs/04_GAME_RULES.md.
 */
class SnakeGameEngine(
    private val diceRoller: DiceRoller = SecureDiceRoller
) : GameEngine<SnakeGameState, EngineAction> {

    override val rulesetId: String = "SNAKE_CLASSIC_V1"

    override fun getInitialState(playerCount: Int, seed: Long): SnakeGameState {
        require(playerCount in 2..4) { "Player count must be between 2 and 4, got $playerCount" }

        val colors = when (playerCount) {
            2 -> listOf(PlayerColor.RED, PlayerColor.YELLOW)
            3 -> listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW)
            4 -> listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE)
            else -> listOf(PlayerColor.RED, PlayerColor.YELLOW)
        }

        val players = colors.mapIndexed { index, color ->
            val player = Player(
                id = "player_${color.name.lowercase()}",
                displayName = color.name.lowercase().replaceFirstChar { it.uppercase() },
                seatIndex = index,
                color = color
            )
            SnakePlayerState.initial(player)
        }

        return SnakeGameState(
            matchId = "snake_${if (seed != 0L) seed else System.currentTimeMillis()}",
            players = players,
            activeSeatIndex = 0,
            currentRoll = null,
            phase = SnakeTurnPhase.WAITING_FOR_ROLL,
            roundCount = 1,
            winners = emptyList(),
            isGameOver = false
        )
    }

    override fun computeLegalMoves(state: SnakeGameState, rollValue: Int): List<Token> {
        if (state.isGameOver) return emptyList()
        val active = state.activePlayer
        if (active.isFinished) return emptyList()

        return listOf(
            Token(
                id = "snake_token_${active.seatIndex}",
                color = active.color,
                tokenIndex = 0,
                state = if (active.currentSquare == 100) TokenState.FINISHED else TokenState.ON_TRACK,
                trackPosition = active.currentSquare,
                stepsMoved = active.currentSquare
            )
        )
    }

    override fun reduce(
        state: SnakeGameState,
        action: EngineAction,
        rollValue: Int?
    ): EngineResult<SnakeGameState> {
        val snakeAction = when (action) {
            is EngineAction.RollDice -> SnakeAction.RollDice(forcedValue = rollValue)
            is EngineAction.Forfeit -> SnakeAction.PassTurn
            is EngineAction.Timeout -> SnakeAction.PassTurn
            else -> SnakeAction.RollDice(forcedValue = rollValue)
        }

        val (nextState, events) = step(state, snakeAction)

        val coreEvents = events.mapNotNull { it.toCoreEvent() }
        return EngineResult(
            state = nextState,
            events = coreEvents,
            isTerminal = nextState.isGameOver
        )
    }

    fun step(
        state: SnakeGameState,
        action: SnakeAction
    ): Pair<SnakeGameState, List<SnakeEvent>> {
        return SnakeStateReducer.reduce(state, action, diceRoller)
    }

    private fun SnakeEvent.toCoreEvent(): GameEvent? = when (this) {
        is SnakeEvent.DiceRolled -> GameEvent.DiceRolled(
            seatIndex = seatIndex,
            value = value,
            consecutiveSixes = consecutiveSixes,
            bonusRollEarned = consecutiveSixes in 1..2
        )
        is SnakeEvent.TokenAdvanced -> GameEvent.TokenMoved(
            seatIndex = seatIndex,
            tokenId = "snake_token_$seatIndex",
            fromPosition = fromSquare,
            toPosition = toSquare
        )
        is SnakeEvent.LadderClimbed -> GameEvent.LadderClimbed(
            seatIndex = seatIndex,
            fromSquare = baseSquare,
            toSquare = topSquare
        )
        is SnakeEvent.SnakeDropped -> GameEvent.SnakeDropped(
            seatIndex = seatIndex,
            fromSquare = headSquare,
            toSquare = tailSquare
        )
        is SnakeEvent.ThreeSixesPenalty -> GameEvent.ConsecutiveSixPenalty(
            seatIndex = seatIndex
        )
        is SnakeEvent.PlayerFinished -> GameEvent.TokenFinished(
            seatIndex = seatIndex,
            tokenId = "snake_token_$seatIndex"
        )
        is SnakeEvent.MatchCompleted -> GameEvent.MatchCompleted(
            winnerSeatIndex = 0,
            placements = standings.map { it.ordinal }
        )
        else -> null
    }
}
