package game.ludora.core.common.feedback

/**
 * Haptic tactile vibration patterns for physical tactile feedback.
 */
enum class HapticPattern {
    TICK,           // Extremely short tick for step and button press
    CLICK,          // Light tactile confirmation
    DICE_BOUNCE,    // Crisp double bounce pulse
    CAPTURE_IMPACT, // Heavy impact pulse
    HAZARD_RUMBLE,  // Rapid warning vibration for snake or bomb
    VICTORY_PULSE   // Celebratory multi-burst rhythm
}

/**
 * User-configurable vibration intensity levels.
 */
enum class HapticIntensity {
    OFF,
    LIGHT,
    MEDIUM,
    STRONG
}

/**
 * Manages tactile vibration events and intensity scaling.
 */
class HapticFeedbackManager(
    var intensity: HapticIntensity = HapticIntensity.MEDIUM
) {
    var triggeredCount: Int = 0
        private set

    var lastTriggeredPattern: HapticPattern? = null
        private set

    fun trigger(pattern: HapticPattern) {
        if (intensity == HapticIntensity.OFF) return
        triggeredCount++
        lastTriggeredPattern = pattern
    }

    /**
     * Returns the vibration timings in milliseconds corresponding to the pattern.
     */
    fun getVibrationTimingMs(pattern: HapticPattern): LongArray {
        return when (pattern) {
            HapticPattern.TICK -> longArrayOf(0, 15)
            HapticPattern.CLICK -> longArrayOf(0, 30)
            HapticPattern.DICE_BOUNCE -> longArrayOf(0, 25, 40, 35)
            HapticPattern.CAPTURE_IMPACT -> longArrayOf(0, 70, 30, 90)
            HapticPattern.HAZARD_RUMBLE -> longArrayOf(0, 40, 20, 40, 20, 60)
            HapticPattern.VICTORY_PULSE -> longArrayOf(0, 80, 50, 80, 50, 140)
        }
    }

    /**
     * Returns the vibration amplitude scaling based on user intensity configuration (0 to 255).
     */
    fun getAmplitudeScale(): Float {
        return when (intensity) {
            HapticIntensity.OFF -> 0f
            HapticIntensity.LIGHT -> 0.4f
            HapticIntensity.MEDIUM -> 0.75f
            HapticIntensity.STRONG -> 1.0f
        }
    }
}
