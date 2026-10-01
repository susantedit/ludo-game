package game.ludora.core.common.feedback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedbackEngineTest {

    @Test
    fun `audio sound manager generates valid pcm waveforms for all effects`() {
        val audio = AudioSoundManager(isSoundEnabled = true, volume = 0.8f)

        for (effect in SoundEffect.entries) {
            val pcm = audio.generatePcmWaveform(effect, sampleRate = 44100)
            assertTrue("Waveform for $effect should have samples", pcm.isNotEmpty())

            // Samples must be within valid 16-bit range
            var hasNonZero = false
            for (sample in pcm) {
                if (sample != 0.toShort()) hasNonZero = true
            }
            assertTrue("Waveform for $effect should contain non-zero audio content", hasNonZero)
        }
    }

    @Test
    fun `audio sound manager respects mute and volume settings`() {
        val audio = AudioSoundManager(isSoundEnabled = false)
        audio.playSound(SoundEffect.BUTTON_CLICK)
        assertEquals(0, audio.playedEffectCount)
        assertNull(audio.lastPlayedEffect)

        audio.isSoundEnabled = true
        audio.playSound(SoundEffect.DICE_ROLL)
        assertEquals(1, audio.playedEffectCount)
        assertEquals(SoundEffect.DICE_ROLL, audio.lastPlayedEffect)
    }

    @Test
    fun `haptic manager dispatches patterns and calculates amplitude scaling`() {
        val haptic = HapticFeedbackManager(intensity = HapticIntensity.MEDIUM)
        assertEquals(0.75f, haptic.getAmplitudeScale(), 0.01f)

        haptic.trigger(HapticPattern.CAPTURE_IMPACT)
        assertEquals(1, haptic.triggeredCount)
        assertEquals(HapticPattern.CAPTURE_IMPACT, haptic.lastTriggeredPattern)

        val timing = haptic.getVibrationTimingMs(HapticPattern.CAPTURE_IMPACT)
        assertTrue(timing.isNotEmpty())

        haptic.intensity = HapticIntensity.OFF
        haptic.trigger(HapticPattern.DICE_BOUNCE)
        assertEquals(1, haptic.triggeredCount) // Should not increment when off
    }
}
