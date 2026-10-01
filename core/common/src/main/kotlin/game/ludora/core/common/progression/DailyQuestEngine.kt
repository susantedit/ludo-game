package game.ludora.core.common.progression

import game.ludora.core.model.DailyQuest
import game.ludora.core.model.LocalProfile
import game.ludora.core.model.QuestType

/**
 * Pure engine for daily quest tracking, event matching, and reward redemption.
 */
object DailyQuestEngine {

    /**
     * Updates quest progress against gameplay events from a completed match.
     */
    fun recordMatchEvents(
        quests: List<DailyQuest>,
        isWin: Boolean,
        tokensCaptured: Int,
        sixesRolled: Int
    ): List<DailyQuest> {
        return quests.map { quest ->
            if (quest.isCompleted) return@map quest

            val increment = when (quest.type) {
                QuestType.WIN_MATCH -> if (isWin) 1 else 0
                QuestType.CAPTURE_TOKENS -> tokensCaptured
                QuestType.ROLL_SIXES -> sixesRolled
                QuestType.PLAY_MATCH -> 1
            }

            if (increment > 0) {
                quest.copy(
                    currentProgress = (quest.currentProgress + increment).coerceAtMost(quest.targetProgress)
                )
            } else {
                quest
            }
        }
    }

    /**
     * Claims the reward of a completed quest and updates the player's profile coins and XP.
     */
    fun claimQuest(profile: LocalProfile, questId: String): Pair<LocalProfile, DailyQuest?> {
        val targetQuest = profile.activeQuests.firstOrNull { it.id == questId } ?: return profile to null
        if (!targetQuest.isCompleted || targetQuest.isClaimed) return profile to null

        val claimedQuest = targetQuest.copy(isClaimed = true)
        val updatedQuests = profile.activeQuests.map {
            if (it.id == questId) claimedQuest else it
        }

        val newXp = profile.experiencePoints + targetQuest.xpReward
        val newLevel = ProgressionEngine.calculateLevel(newXp)
        val newCoins = profile.coins + targetQuest.coinReward

        val updatedProfile = profile.copy(
            experiencePoints = newXp,
            level = newLevel,
            coins = newCoins,
            activeQuests = updatedQuests
        )

        return updatedProfile to claimedQuest
    }
}
