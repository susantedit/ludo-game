package game.ludora.engine.snake.model

import game.ludora.core.model.PlayerColor
import game.ludora.engine.core.GameEvent
import kotlinx.serialization.Serializable

@Serializable
sealed interface SnakeEvent : GameEvent {

    @Serializable
    data class DiceRolled(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val value: Int,
        val consecutiveSixes: Int
    ) : SnakeEvent

    @Serializable
    data class TokenAdvanced(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val fromSquare: Int,
        val toSquare: Int
    ) : SnakeEvent

    @Serializable
    data class LadderClimbed(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val baseSquare: Int,
        val topSquare: Int
    ) : SnakeEvent

    @Serializable
    data class SnakeDropped(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val headSquare: Int,
        val tailSquare: Int
    ) : SnakeEvent

    @Serializable
    data class OvershootStalled(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val currentSquare: Int,
        val roll: Int
    ) : SnakeEvent

    @Serializable
    data class ThreeSixesPenalty(
        val playerColor: PlayerColor,
        val seatIndex: Int
    ) : SnakeEvent

    @Serializable
    data class BonusRollGranted(
        val playerColor: PlayerColor,
        val seatIndex: Int
    ) : SnakeEvent

    @Serializable
    data class TurnPassed(
        val previousSeatIndex: Int,
        val nextSeatIndex: Int,
        val nextPlayerColor: PlayerColor,
        val roundCount: Int
    ) : SnakeEvent

    @Serializable
    data class PlayerFinished(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val rank: Int
    ) : SnakeEvent

    @Serializable
    data class MatchCompleted(
        val standings: List<PlayerColor>,
        val reason: String
    ) : SnakeEvent
}
