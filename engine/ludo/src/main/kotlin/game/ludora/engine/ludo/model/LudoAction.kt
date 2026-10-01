package game.ludora.engine.ludo.model

import kotlinx.serialization.Serializable

/**
 * User or AI player actions in a Ludo match.
 */
@Serializable
sealed interface LudoAction {

    /**
     * Active player initiates a dice roll.
     * @param forcedValue Optional deterministic override for testing or replay.
     */
    @Serializable
    data class RollDice(val forcedValue: Int? = null) : LudoAction

    /**
     * Active player selects a token to advance following a roll.
     * @param tokenId Index (0 to 3) of the token to move.
     */
    @Serializable
    data class SelectMove(val tokenId: Int) : LudoAction

    /**
     * Passes the turn when zero legal moves are available.
     */
    @Serializable
    data object PassTurn : LudoAction
}
