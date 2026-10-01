package game.ludora.core.designsystem.theme

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ColorContrastTest {

    private fun linearize(channel: Float): Double {
        return if (channel <= 0.04045f) {
            channel / 12.92
        } else {
            ((channel + 0.055) / 1.055).toDouble().pow(2.4)
        }
    }

    private fun luminance(r: Float, g: Float, b: Float): Double {
        return 0.2126 * linearize(r) + 0.7152 * linearize(g) + 0.0722 * linearize(b)
    }

    private fun contrastRatio(fgLuminance: Double, bgLuminance: Double): Double {
        val l1 = max(fgLuminance, bgLuminance)
        val l2 = min(fgLuminance, bgLuminance)
        return (l1 + 0.05) / (l2 + 0.05)
    }

    @Test
    fun testPrimaryTextContrastOnSurfaces() {
        // TextPrimary: #F8FAFC (248, 250, 252)
        val fgLum = luminance(248f / 255f, 250f / 255f, 252f / 255f)

        // Background: #0B0F19 (11, 15, 25)
        val bgLum = luminance(11f / 255f, 15f / 255f, 25f / 255f)
        val ratioOnBg = contrastRatio(fgLum, bgLum)
        assertTrue("Primary text on background must exceed 7.0:1, got $ratioOnBg", ratioOnBg >= 7.0)

        // SurfaceDark: #0F172A (15, 23, 42)
        val surfaceLum = luminance(15f / 255f, 23f / 255f, 42f / 255f)
        val ratioOnSurface = contrastRatio(fgLum, surfaceLum)
        assertTrue("Primary text on surface must exceed 7.0:1, got $ratioOnSurface", ratioOnSurface >= 7.0)

        // CardElevated: #1E293B (30, 41, 59)
        val cardLum = luminance(30f / 255f, 41f / 255f, 59f / 255f)
        val ratioOnCard = contrastRatio(fgLum, cardLum)
        assertTrue("Primary text on elevated card must exceed 4.5:1, got $ratioOnCard", ratioOnCard >= 4.5)
    }

    @Test
    fun testSecondaryTextContrastOnSurfaces() {
        // TextSecondary: #94A3B8 (148, 163, 184)
        val fgLum = luminance(148f / 255f, 163f / 255f, 184f / 255f)

        // SurfaceDark: #0F172A
        val surfaceLum = luminance(15f / 255f, 23f / 255f, 42f / 255f)
        val ratio = contrastRatio(fgLum, surfaceLum)
        assertTrue("Secondary text must exceed 4.5:1 on dark surface, got $ratio", ratio >= 4.5)
    }
}
