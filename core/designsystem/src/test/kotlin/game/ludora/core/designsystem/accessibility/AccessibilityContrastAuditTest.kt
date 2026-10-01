package game.ludora.core.designsystem.accessibility

import androidx.compose.ui.graphics.Color
import game.ludora.core.designsystem.theme.toAccessibleColor
import game.ludora.core.model.ColorBlindMode
import game.ludora.core.model.PlayerColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * WCAG 2.1 AA accessibility contrast audit across all color-blind perception modes.
 */
class AccessibilityContrastAuditTest {

    private fun linearize(channel: Float): Double {
        return if (channel <= 0.04045f) {
            channel / 12.92
        } else {
            ((channel + 0.055) / 1.055).toDouble().pow(2.4)
        }
    }

    private fun luminance(color: Color): Double {
        return 0.2126 * linearize(color.red) + 0.7152 * linearize(color.green) + 0.0722 * linearize(color.blue)
    }

    private fun contrastRatio(c1: Color, c2: Color): Double {
        val l1 = max(luminance(c1), luminance(c2))
        val l2 = min(luminance(c1), luminance(c2))
        return (l1 + 0.05) / (l2 + 0.05)
    }

    @Test
    fun `all color-blind modes maintain at least 3 to 1 contrast on obsidian background`() {
        val background = Color(0xFF0B0F19)
        val surface = Color(0xFF0F172A)

        for (mode in ColorBlindMode.entries) {
            for (playerColor in PlayerColor.entries) {
                val color = playerColor.toAccessibleColor(mode)
                val ratioOnBg = contrastRatio(color, background)
                val ratioOnSurface = contrastRatio(color, surface)

                assertTrue(
                    "Mode $mode: $playerColor on background must meet 3.0:1 contrast, was $ratioOnBg",
                    ratioOnBg >= 3.0
                )
                assertTrue(
                    "Mode $mode: $playerColor on surface must meet 3.0:1 contrast, was $ratioOnSurface",
                    ratioOnSurface >= 3.0
                )
            }
        }
    }

    @Test
    fun `color blind mode colors remain distinct from each other within each palette`() {
        for (mode in ColorBlindMode.entries) {
            val colors = PlayerColor.entries.map { it.toAccessibleColor(mode) }
            val distinctCount = colors.map { it.value }.distinct().size
            assertEquals(
                "All 4 player colors must have distinct hex values in $mode mode",
                4,
                distinctCount
            )
        }
    }
}
