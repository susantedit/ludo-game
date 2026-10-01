package game.ludora.engine.core

import game.ludora.core.model.PlayerColor
import game.ludora.core.model.Token
import kotlinx.serialization.Serializable

@Serializable
enum class TurnPhase {
    WAITING_FOR_ROLL,
    WAITING_FOR_MOVE,
    RESOLVING_MOVE,
    TURN_COMPLETED,
    MATCH_OVER
}

sealed interface EngineAction {
    val seatIndex: Int

    data class RollDice(override val seatIndex: Int) : EngineAction
    data class SelectToken(override val seatIndex: Int, val tokenId: String) : EngineAction
    data class ReleaseToken(override val seatIndex: Int, val tokenId: String) : EngineAction
    data class Forfeit(override val seatIndex: Int) : EngineAction
    data class Timeout(override val seatIndex: Int) : EngineAction
}

sealed interface GameEvent {
    val timestamp: Long

    data class TurnStarted(
        val seatIndex: Int,
        val turnDeadline: Long?,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent

    data class DiceRolled(
        val seatIndex: Int,
        val value: Int,
        val consecutiveSixes: Int,
        val bonusRollEarned: Boolean,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent

    data class TokenMoved(
        val seatIndex: Int,
        val tokenId: String,
        val fromPosition: Int,
        val toPosition: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent

    data class TokenCaptured(
        val attackerSeatIndex: Int,
        val attackerTokenId: String,
        val victimSeatIndex: Int,
        val victimTokenId: String,
        val position: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent

    data class TokenFinished(
        val seatIndex: Int,
        val tokenId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent

    data class LadderClimbed(
        val seatIndex: Int,
        val fromSquare: Int,
        val toSquare: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent

    data class SnakeDropped(
        val seatIndex: Int,
        val fromSquare: Int,
        val toSquare: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent

    data class ConsecutiveSixPenalty(
        val seatIndex: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent

    data class MatchCompleted(
        val winnerSeatIndex: Int,
        val placements: List<Int>,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent
}

data class EngineResult<State>(
    val state: State,
    val events: List<GameEvent> = emptyList(),
    val isTerminal: Boolean = false
)

interface GameEngine<State, Action : EngineAction> {
    val rulesetId: String
    fun getInitialState(playerCount: Int, seed: Long = 0L): State
    fun computeLegalMoves(state: State, rollValue: Int): List<Token>
    fun reduce(state: State, action: Action, rollValue: Int? = null): EngineResult<State>
}
