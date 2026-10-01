package game.ludora.core.designsystem.component

import game.ludora.core.model.PlayerColor
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerSymbolTest {

    @Test
    fun testAccessibilitySymbolsForAllPlayerColors() {
        assertEquals(PlayerAccessibilitySymbol.CIRCLE, PlayerAccessibilitySymbol.from(PlayerColor.RED))
        assertEquals(PlayerAccessibilitySymbol.TRIANGLE, PlayerAccessibilitySymbol.from(PlayerColor.GREEN))
        assertEquals(PlayerAccessibilitySymbol.DIAMOND, PlayerAccessibilitySymbol.from(PlayerColor.YELLOW))
        assertEquals(PlayerAccessibilitySymbol.SQUARE, PlayerAccessibilitySymbol.from(PlayerColor.BLUE))
    }

    @Test
    fun testSymbolGlyphs() {
        assertEquals("●", PlayerAccessibilitySymbol.CIRCLE.glyph)
        assertEquals("▲", PlayerAccessibilitySymbol.TRIANGLE.glyph)
        assertEquals("◆", PlayerAccessibilitySymbol.DIAMOND.glyph)
        assertEquals("■", PlayerAccessibilitySymbol.SQUARE.glyph)
    }
}
