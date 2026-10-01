package game.ludora.core.common.feedback

import kotlin.math.PI
import kotlin.math.sin

/**
 * Sound effect events across Ludora gameplay.
 */
enum class SoundEffect {
    BUTTON_CLICK,
    DICE_ROLL,
    DICE_SETTLE,
    TOKEN_STEP,
    TOKEN_CAPTURE,
    LADDER_CLIMB,
    SNAKE_DESCENT,
    VICTORY_FANFARE,
    TURN_ALERT
}

/**
 * Procedural audio waveform synthesizer and sound manager.
 * Generates 16-bit PCM waveforms mathematically without external asset dependencies.
 */
class AudioSoundManager(
    var isSoundEnabled: Boolean = true,
    var volume: Float = 0.8f
) {
    var playedEffectCount: Int = 0
        private set

    var lastPlayedEffect: SoundEffect? = null
        private set

    /**
     * Plays a sound effect if audio is enabled and volume > 0.
     */
    fun playSound(effect: SoundEffect) {
        if (!isSoundEnabled || volume <= 0f) return
        playedEffectCount++
        lastPlayedEffect = effect
    }

    /**
     * Generates a 16-bit PCM mono audio sample buffer for a given sound effect.
     * Sample rate: 44,100 Hz.
     */
    fun generatePcmWaveform(effect: SoundEffect, sampleRate: Int = 44100): ShortArray {
        return when (effect) {
            SoundEffect.BUTTON_CLICK -> generateTone(frequency = 880f, durationMs = 40, sampleRate = sampleRate, decay = true)
            SoundEffect.DICE_ROLL -> generateNoiseClick(durationMs = 80, sampleRate = sampleRate)
            SoundEffect.DICE_SETTLE -> generateTone(frequency = 220f, durationMs = 120, sampleRate = sampleRate, decay = true)
            SoundEffect.TOKEN_STEP -> generateTone(frequency = 440f, durationMs = 50, sampleRate = sampleRate, decay = true)
            SoundEffect.TOKEN_CAPTURE -> generateSweep(startFreq = 300f, endFreq = 100f, durationMs = 250, sampleRate = sampleRate)
            SoundEffect.LADDER_CLIMB -> generateArpeggio(frequencies = floatArrayOf(261.63f, 329.63f, 392.00f, 523.25f), stepDurationMs = 80, sampleRate = sampleRate)
            SoundEffect.SNAKE_DESCENT -> generateSweep(startFreq = 520f, endFreq = 160f, durationMs = 400, sampleRate = sampleRate)
            SoundEffect.VICTORY_FANFARE -> generateChord(frequencies = floatArrayOf(523.25f, 659.25f, 783.99f), durationMs = 800, sampleRate = sampleRate)
            SoundEffect.TURN_ALERT -> generateTone(frequency = 587.33f, durationMs = 150, sampleRate = sampleRate, decay = true)
        }
    }

    private fun generateTone(frequency: Float, durationMs: Int, sampleRate: Int, decay: Boolean): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toFloat() / sampleRate
            val angle = 2.0 * PI * frequency * t
            val envelope = if (decay) 1f - (i.toFloat() / totalSamples) else 1f
            val sample = (sin(angle) * Short.MAX_VALUE * envelope * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateNoiseClick(durationMs: Int, sampleRate: Int): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val buffer = ShortArray(totalSamples)
        var lastRandom = 0.5f
        for (i in 0 until totalSamples) {
            val random = (Math.random().toFloat() * 2f - 1f)
            lastRandom = (lastRandom + random) * 0.5f
            val envelope = 1f - (i.toFloat() / totalSamples)
            val sample = (lastRandom * Short.MAX_VALUE * envelope * 0.5f * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateSweep(startFreq: Float, endFreq: Float, durationMs: Int, sampleRate: Int): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val buffer = ShortArray(totalSamples)
        var phase = 0.0
        for (i in 0 until totalSamples) {
            val progress = i.toFloat() / totalSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * PI * currentFreq / sampleRate
            val envelope = 1f - (progress * 0.8f)
            val sample = (sin(phase) * Short.MAX_VALUE * envelope * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateArpeggio(frequencies: FloatArray, stepDurationMs: Int, sampleRate: Int): ShortArray {
        val stepSamples = (sampleRate * (stepDurationMs / 1000f)).toInt()
        val totalSamples = stepSamples * frequencies.size
        val buffer = ShortArray(totalSamples)
        for ((idx, freq) in frequencies.withIndex()) {
            val offset = idx * stepSamples
            for (i in 0 until stepSamples) {
                val t = i.toFloat() / sampleRate
                val angle = 2.0 * PI * freq * t
                val envelope = 1f - (i.toFloat() / stepSamples * 0.5f)
                val sample = (sin(angle) * Short.MAX_VALUE * envelope * volume).toInt()
                buffer[offset + i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        return buffer
    }

    private fun generateChord(frequencies: FloatArray, durationMs: Int, sampleRate: Int): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toFloat() / sampleRate
            var sum = 0.0
            for (freq in frequencies) {
                sum += sin(2.0 * PI * freq * t)
            }
            sum /= frequencies.size
            val envelope = 1f - (i.toFloat() / totalSamples * 0.7f)
            val sample = (sum * Short.MAX_VALUE * envelope * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
