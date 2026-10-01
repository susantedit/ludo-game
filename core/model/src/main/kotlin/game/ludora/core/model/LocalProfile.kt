package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalProfile(
    val profileId: String,
    val displayName: String = "Player",
    val avatarId: String = "mascot_shiba_inu",
    val frameId: String = "frame_default",
    val experiencePoints: Long = 0L,
    val level: Int = 1,
    val coins: Long = 150L,
    val statsMatchesPlayed: Int = 0,
    val statsMatchesWon: Int = 0,
    val statsTokensCaptured: Int = 0,
    val equippedDiceSkinId: String = "dice_classic",
    val equippedTokenStyleId: String = "token_classic",
    val equippedBoardThemeId: String = "board_obsidian",
    val unlockedCosmeticIds: List<String> = listOf("dice_classic", "token_classic", "board_obsidian"),
    val activeQuests: List<DailyQuest> = DailyQuest.defaultQuests(),
    val isAdFree: Boolean = false,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
) {
    val winRate: Float
        get() = if (statsMatchesPlayed > 0) {
            (statsMatchesWon.toFloat() / statsMatchesPlayed.toFloat()) * 100f
        } else {
            0f
        }

    val xpForNextLevel: Long
        get() = level * 150L

    val currentLevelXpProgress: Long
        get() {
            var accumulated = 0L
            for (lvl in 1 until level) {
                accumulated += lvl * 150L
            }
            return (experiencePoints - accumulated).coerceAtLeast(0L)
        }

    val levelProgressFraction: Float
        get() = (currentLevelXpProgress.toFloat() / xpForNextLevel.toFloat()).coerceIn(0f, 1f)
}
