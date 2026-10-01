package game.ludora.engine.ludo.model

import game.ludora.core.model.PlayerColor

/**
 * Constants and pure coordinate math for the 52-step standard Ludo board.
 */
object LudoBoard {

    const val TRACK_SIZE = 52
    const val HOME_PATH_SIZE = 5

    /**
     * The 8 permanent safe squares on the common track:
     * 4 start squares: 0, 13, 26, 39
     * 4 star squares: 8, 21, 34, 47
     */
    val SAFE_SQUARES: Set<Int> = setOf(0, 8, 13, 21, 26, 34, 39, 47)

    /**
     * Starting perimeter step index for each color.
     */
    fun startStep(color: PlayerColor): Int = when (color) {
        PlayerColor.RED -> 0
        PlayerColor.GREEN -> 13
        PlayerColor.YELLOW -> 26
        PlayerColor.BLUE -> 39
    }

    /**
     * The track step index immediately preceding entry into the colored home path.
     */
    fun homeEntranceStep(color: PlayerColor): Int = when (color) {
        PlayerColor.RED -> 50
        PlayerColor.GREEN -> 11
        PlayerColor.YELLOW -> 24
        PlayerColor.BLUE -> 37
    }

    /**
     * Checks if a common track step is a safe square.
     */
    fun isSafeSquare(stepIndex: Int): Boolean = stepIndex in SAFE_SQUARES

    /**
     * Calculates the destination position given current position, roll value, and player color.
     * Returns null if the move is invalid or overshoots the Center Goal.
     */
    fun calculateNextPosition(
        current: LudoPosition,
        roll: Int,
        color: PlayerColor
    ): LudoPosition? {
        if (roll !in 1..6) return null

        return when (current) {
            is LudoPosition.InBase -> {
                if (roll == 6) LudoPosition.OnTrack(startStep(color)) else null
            }
            is LudoPosition.OnTrack -> {
                val currentDistance = (current.stepIndex - startStep(color) + TRACK_SIZE) % TRACK_SIZE
                val newDistance = currentDistance + roll

                when {
                    newDistance <= 50 -> {
                        LudoPosition.OnTrack((current.stepIndex + roll) % TRACK_SIZE)
                    }
                    newDistance in 51..55 -> {
                        val homeStep = newDistance - 50
                        LudoPosition.InHomePath(homeStep)
                    }
                    newDistance == 56 -> {
                        LudoPosition.Finished
                    }
                    else -> null // Overshoot
                }
            }
            is LudoPosition.InHomePath -> {
                val newStep = current.stepIndex + roll
                when {
                    newStep in 1..HOME_PATH_SIZE -> LudoPosition.InHomePath(newStep)
                    newStep == HOME_PATH_SIZE + 1 -> LudoPosition.Finished
                    else -> null // Overshoot
                }
            }
            is LudoPosition.Finished -> null
        }
    }

    /**
     * Computes remaining distance to Center Goal. Used for anti-stall ranking and tie breaking.
     */
    fun distanceToGoal(position: LudoPosition, color: PlayerColor): Int = when (position) {
        is LudoPosition.InBase -> 57
        is LudoPosition.OnTrack -> {
            val trackDistance = (position.stepIndex - startStep(color) + TRACK_SIZE) % TRACK_SIZE
            56 - trackDistance
        }
        is LudoPosition.InHomePath -> 6 - position.stepIndex
        is LudoPosition.Finished -> 0
    }
}
