package game.ludora.engine.ludo.model

import kotlinx.serialization.Serializable

/**
 * Represents the discrete position of a Ludo token on the board.
 */
@Serializable
sealed interface LudoPosition {

    /**
     * Token is inside the home base quadrant.
     * @param slotIndex Base slot index (0 to 3).
     */
    @Serializable
    data class InBase(val slotIndex: Int) : LudoPosition

    /**
     * Token is navigating the common 52-step perimeter track.
     * @param stepIndex Track step index (0 to 51).
     */
    @Serializable
    data class OnTrack(val stepIndex: Int) : LudoPosition

    /**
     * Token is navigating the dedicated 5-step colored home path.
     * @param stepIndex Home path step index (1 to 5).
     */
    @Serializable
    data class InHomePath(val stepIndex: Int) : LudoPosition

    /**
     * Token has reached the Center Goal and is finished.
     */
    @Serializable
    data object Finished : LudoPosition
}
