package game.ludora.core.designsystem.mascot

import kotlin.math.PI
import kotlin.math.atan2

/**
 * 9 discrete head gaze directions corresponding to cells 0..8 on a 3x3 sprite sheet.
 */
enum class MascotDirection(val cellIndex: Int) {
    UP_LEFT(0),
    UP(1),
    UP_RIGHT(2),
    LEFT(3),
    CENTER(4),
    RIGHT(5),
    DOWN_LEFT(6),
    DOWN(7),
    DOWN_RIGHT(8);

    companion object {
        // Clockwise from right (matching atan2 with screen Y pointing down)
        private val CLOCKWISE = listOf(
            RIGHT,
            DOWN_RIGHT,
            DOWN,
            DOWN_LEFT,
            LEFT,
            UP_LEFT,
            UP,
            UP_RIGHT
        )
        private const val SECTOR = (PI * 2) / 8.0
        private const val DEAD_ZONE_PX = 40.0

        fun fromDelta(dx: Float, dy: Float): MascotDirection {
            val distance = Math.hypot(dx.toDouble(), dy.toDouble())
            if (distance < DEAD_ZONE_PX) return CENTER

            val angle = atan2(dy.toDouble(), dx.toDouble())
            val normalized = (angle + PI * 2) % (PI * 2)
            val sectorIndex = (((normalized + SECTOR / 2) % (PI * 2)) / SECTOR).toInt() % 8
            return CLOCKWISE[sectorIndex]
        }
    }
}

/**
 * 9 emotional reactions corresponding to cells 0..8 on a 3x3 sprite sheet.
 */
enum class MascotReaction(val cellIndex: Int) {
    BLINK(0),
    HEART(1),
    SPARKLE(2),
    SURPRISED(3),
    WINK(4),
    BASHFUL(5),
    SLEEPY(6),
    DIZZY(7),
    DELIGHTED(8);

    companion object {
        val TAP_REACTIONS = listOf(HEART, SPARKLE, DELIGHTED, WINK, SURPRISED)
    }
}
