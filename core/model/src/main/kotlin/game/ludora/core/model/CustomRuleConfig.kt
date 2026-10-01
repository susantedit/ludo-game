package game.ludora.core.model

import kotlinx.serialization.Serializable

enum class BoardTheme {
    CLASSIC_OBSIDIAN,
    KATHMANDU,
    PIRATE_WORLD,
    NEON_CYBERPUNK
}

/**
 * User-generated and custom match configuration.
 * Enables custom game variants as defined in idea.md (e.g. Kathmandu board, 15s timer, chaos modifiers).
 */
@Serializable
data class CustomRuleConfig(
    val ruleName: String = "Kathmandu Grand Prix",
    val playerCount: Int = 4,
    val turnTimerSeconds: Int = 15,
    val doubleSixExtraTurn: Boolean = true,
    val consecutiveSixLimit: Int = 3,
    val powerCardsPerPlayer: Int = 2,
    val chaosModeEnabled: Boolean = true,
    val chaosFrequencyTurns: Int = 3,
    val boardTheme: BoardTheme = BoardTheme.KATHMANDU,
    val exactFinishRequired: Boolean = true
) {
    init {
        require(playerCount in 2..4) { "Player count must be between 2 and 4 (was $playerCount)" }
        require(turnTimerSeconds in 0..60) { "Turn timer must be between 0 and 60 seconds (was $turnTimerSeconds)" }
        require(powerCardsPerPlayer in 0..5) { "Power cards per player must be between 0 and 5" }
        require(chaosFrequencyTurns >= 1) { "Chaos frequency must be at least 1 turn" }
    }

    companion object {
        val DEFAULT_KATHMANDU = CustomRuleConfig(
            ruleName = "Kathmandu Odyssey",
            playerCount = 4,
            turnTimerSeconds = 15,
            doubleSixExtraTurn = true,
            powerCardsPerPlayer = 2,
            chaosModeEnabled = true,
            boardTheme = BoardTheme.KATHMANDU
        )

        val SPEED_LUDO = CustomRuleConfig(
            ruleName = "Lightning Rush",
            playerCount = 4,
            turnTimerSeconds = 10,
            doubleSixExtraTurn = true,
            powerCardsPerPlayer = 0,
            chaosModeEnabled = false,
            boardTheme = BoardTheme.CLASSIC_OBSIDIAN
        )
    }
}
