package game.ludora.engine.ludo.model

import game.ludora.core.model.PlayerColor
import game.ludora.engine.core.GameEvent
import kotlinx.serialization.Serializable

@Serializable
enum class BonusRollReason {
    ROLLED_SIX,
    CAPTURED_OPPONENT,
    REACHED_GOAL
}

/**
 * Domain events emitted by the Ludo engine during state transitions.
 */
@Serializable
sealed interface LudoEvent : GameEvent {

    @Serializable
    data class DiceRolled(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val value: Int,
        val consecutiveSixes: Int
    ) : LudoEvent

    @Serializable
    data class ThreeSixesPenalty(
        val playerColor: PlayerColor,
        val seatIndex: Int
    ) : LudoEvent

    @Serializable
    data class NoLegalMoves(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val roll: Int
    ) : LudoEvent

    @Serializable
    data class TokenMoved(
        val playerColor: PlayerColor,
        val tokenId: Int,
        val from: LudoPosition,
        val to: LudoPosition
    ) : LudoEvent

    @Serializable
    data class TokenReleased(
        val playerColor: PlayerColor,
        val tokenId: Int,
        val startStep: Int
    ) : LudoEvent

    @Serializable
    data class TokenCaptured(
        val capturingColor: PlayerColor,
        val capturedColor: PlayerColor,
        val capturedTokenId: Int,
        val stepIndex: Int
    ) : LudoEvent

    @Serializable
    data class BonusRollGranted(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val reason: BonusRollReason
    ) : LudoEvent

    @Serializable
    data class TurnPassed(
        val previousSeatIndex: Int,
        val nextSeatIndex: Int,
        val nextPlayerColor: PlayerColor,
        val roundCount: Int
    ) : LudoEvent

    @Serializable
    data class PlayerFinished(
        val playerColor: PlayerColor,
        val seatIndex: Int,
        val rank: Int
    ) : LudoEvent

    @Serializable
    data class MatchCompleted(
        val standings: List<PlayerColor>,
        val reason: String
    ) : LudoEvent
}
