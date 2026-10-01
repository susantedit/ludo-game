package game.ludora.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomRuleConfigTest {

    @Test
    fun `default kathmandu configuration is valid and has expected presets`() {
        val config = CustomRuleConfig.DEFAULT_KATHMANDU
        assertEquals("Kathmandu Odyssey", config.ruleName)
        assertEquals(4, config.playerCount)
        assertEquals(15, config.turnTimerSeconds)
        assertEquals(BoardTheme.KATHMANDU, config.boardTheme)
        assertTrue(config.doubleSixExtraTurn)
        assertTrue(config.chaosModeEnabled)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `playerCount below 2 throws exception`() {
        CustomRuleConfig(playerCount = 1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `turnTimerSeconds exceeding 60 throws exception`() {
        CustomRuleConfig(turnTimerSeconds = 61)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `powerCardsPerPlayer exceeding 5 throws exception`() {
        CustomRuleConfig(powerCardsPerPlayer = 6)
    }
}
