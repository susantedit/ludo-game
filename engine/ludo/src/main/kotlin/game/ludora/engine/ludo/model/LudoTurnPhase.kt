package game.ludora.engine.ludo.model

import kotlinx.serialization.Serializable

/**
 * Phase of a player's turn cycle.
 */
@Serializable
enum class LudoTurnPhase {
    /**
     * Active player must roll the die.
     */
    WAITING_FOR_ROLL,

    /**
     * Active player has rolled and must select a legal token to move.
     */
    WAITING_FOR_MOVE,

    /**
     * Match has concluded.
     */
    GAME_OVER
}
