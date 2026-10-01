package game.ludora.core.common.feedback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HighFidelitySoundManagerTest {

    private class FakeResourceProvider(
        private val availableCues: Set<AcousticCue> = emptySet()
    ) : SoundResourceProvider {
        override fun hasResource(cue: AcousticCue): Boolean = cue in availableCues
        override fun getResourceIdentifier(cue: AcousticCue): String? =
            if (cue in availableCues) "raw/sound_${cue.name.lowercase()}" else null
    }

    @Test
    fun `stereo pan maps board position correctly from left to right`() {
        val manager = HighFidelitySoundManager()
        assertEquals(-1.0f, manager.calculateStereoPan(0f, 100f), 0.01f)
        assertEquals(0.0f, manager.calculateStereoPan(50f, 100f), 0.01f)
        assertEquals(1.0f, manager.calculateStereoPan(100f, 100f), 0.01f)
    }

    @Test
    fun `zero volume suppresses cue playback`() {
        val manager = HighFidelitySoundManager()
        manager.volume = 0f
        assertFalse(manager.playAcousticCue(AcousticCue.DICE_RATTLE))
    }

    @Test
    fun `plays acoustic cue via procedural fallback when resource provider has no assets`() {
        val manager = HighFidelitySoundManager(resourceProvider = FakeResourceProvider(emptySet()))
        manager.volume = 1.0f
        assertTrue(manager.playAcousticCue(AcousticCue.TOKEN_TAP))
    }

    @Test
    fun `plays acoustic cue when resource provider has matching asset`() {
        val provider = FakeResourceProvider(setOf(AcousticCue.LADDER_CHIME))
        val manager = HighFidelitySoundManager(resourceProvider = provider)
        assertTrue(manager.playAcousticCue(AcousticCue.LADDER_CHIME))
    }
}
