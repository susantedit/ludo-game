package game.ludora.core.common.hardware

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LowMemoryPolicyTest {

    @Test
    fun `standard device uses full fidelity graphics and particle counts`() {
        val policy = LowMemoryPolicy(isLowRamDevice = false, availableMemoryMb = 4096)

        assertEquals(60, policy.maxConfettiParticles)
        assertFalse(policy.useSimplifiedShadows)
        assertEquals(50, policy.maxInMemoryMatchHistoryRecords)
        assertEquals(1.0f, policy.renderScaleFactor, 0.01f)
    }

    @Test
    fun `low ram device enforces performance fallbacks`() {
        val policy = LowMemoryPolicy(isLowRamDevice = true, availableMemoryMb = 2048)

        assertEquals(20, policy.maxConfettiParticles)
        assertTrue(policy.useSimplifiedShadows)
        assertEquals(25, policy.maxInMemoryMatchHistoryRecords)
        assertEquals(1.0f, policy.renderScaleFactor, 0.01f)
    }

    @Test
    fun `severely constrained device under 1500mb downscales render buffer`() {
        val policy = LowMemoryPolicy(isLowRamDevice = true, availableMemoryMb = 1024)

        assertEquals(0.85f, policy.renderScaleFactor, 0.01f)
        assertEquals(20, policy.maxConfettiParticles)
    }
}
