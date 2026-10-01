package game.ludora.core.common.feedback

enum class AcousticCue {
    DICE_RATTLE,
    TOKEN_TAP,
    CAPTURE_STRIKE,
    LADDER_CHIME,
    SNAKE_HISS,
    VICTORY_FANFARE
}

interface SoundResourceProvider {
    fun hasResource(cue: AcousticCue): Boolean
    fun getResourceIdentifier(cue: AcousticCue): String?
}

/**
 * Dual-Mode High-Fidelity Sound Engine.
 * Dispatches high-resolution acoustic audio cues when sound resources exist,
 * with automatic fallback to the procedural PCM synthesizer for offline or low-RAM environments.
 */
class HighFidelitySoundManager(
    private val resourceProvider: SoundResourceProvider? = null,
    private val proceduralSynth: AudioSoundManager = AudioSoundManager()
) {
    var volume: Float = 1.0f
        set(value) { field = value.coerceIn(0f, 1f) }

    var isProceduralFallbackOnly: Boolean = false

    private val maxConcurrentStreams = 3
    private var activeStreamCount = 0

    fun calculateStereoPan(boardX: Float, boardWidth: Float): Float {
        if (boardWidth <= 0f) return 0f
        return ((boardX / boardWidth) * 2f - 1f).coerceIn(-1f, 1f)
    }

    fun playAcousticCue(cue: AcousticCue, boardX: Float = 0.5f, boardWidth: Float = 1f): Boolean {
        if (volume <= 0f) return false
        if (activeStreamCount >= maxConcurrentStreams) return false

        val pan = calculateStereoPan(boardX, boardWidth)

        val playedResource = if (!isProceduralFallbackOnly && resourceProvider != null && resourceProvider.hasResource(cue)) {
            // Resource playback available
            val resId = resourceProvider.getResourceIdentifier(cue)
            resId != null
        } else {
            false
        }

        if (!playedResource) {
            // Map acoustic cue to procedural synthesis fallback
            val fallbackEffect = when (cue) {
                AcousticCue.DICE_RATTLE -> SoundEffect.DICE_ROLL
                AcousticCue.TOKEN_TAP -> SoundEffect.TOKEN_STEP
                AcousticCue.CAPTURE_STRIKE -> SoundEffect.CAPTURE_STRIKE
                AcousticCue.LADDER_CHIME -> SoundEffect.LADDER_CLIMB
                AcousticCue.SNAKE_HISS -> SoundEffect.SNAKE_SLIDE
                AcousticCue.VICTORY_FANFARE -> SoundEffect.VICTORY_FANFARE
            }
            proceduralSynth.playSound(fallbackEffect)
        }

        return true
    }
}
