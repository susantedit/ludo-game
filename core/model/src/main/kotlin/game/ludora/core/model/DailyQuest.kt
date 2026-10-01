package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class QuestType {
    WIN_MATCH,
    CAPTURE_TOKENS,
    ROLL_SIXES,
    PLAY_MATCH
}

@Serializable
data class DailyQuest(
    val id: String,
    val type: QuestType,
    val title: String,
    val description: String,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val xpReward: Long = 50L,
    val coinReward: Long = 30L,
    val isClaimed: Boolean = false
) {
    val isCompleted: Boolean get() = currentProgress >= targetProgress
    val progressFraction: Float get() = (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f)

    companion object {
        fun defaultQuests(): List<DailyQuest> = listOf(
            DailyQuest(
                id = "quest_win_match",
                type = QuestType.WIN_MATCH,
                title = "Victor's Glory",
                description = "Win 1 match in any game mode",
                currentProgress = 0,
                targetProgress = 1,
                xpReward = 80L,
                coinReward = 50L
            ),
            DailyQuest(
                id = "quest_capture_tokens",
                type = QuestType.CAPTURE_TOKENS,
                title = "Master Striker",
                description = "Capture 2 opponent tokens in Ludo or Remix",
                currentProgress = 0,
                targetProgress = 2,
                xpReward = 60L,
                coinReward = 40L
            ),
            DailyQuest(
                id = "quest_roll_sixes",
                type = QuestType.ROLL_SIXES,
                title = "Lucky Roll",
                description = "Roll a six 3 times in matches",
                currentProgress = 0,
                targetProgress = 3,
                xpReward = 50L,
                coinReward = 30L
            ),
            DailyQuest(
                id = "quest_play_match",
                type = QuestType.PLAY_MATCH,
                title = "Board Explorer",
                description = "Play 2 complete matches",
                currentProgress = 0,
                targetProgress = 2,
                xpReward = 70L,
                coinReward = 45L
            )
        )
    }
}
