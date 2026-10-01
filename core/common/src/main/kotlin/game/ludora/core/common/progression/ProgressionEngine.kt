package game.ludora.core.common.progression

import game.ludora.core.model.CosmeticCategory
import game.ludora.core.model.CosmeticItem
import game.ludora.core.model.LocalProfile
import game.ludora.core.model.MatchReward

/**
 * Pure progression engine governing experience point scaling, level milestones,
 * match rewards, and cosmetic unlock verification.
 */
object ProgressionEngine {

    const val BASE_XP_PER_LEVEL = 150L

    /**
     * Calculates the player level given total cumulative experience points.
     */
    fun calculateLevel(totalXp: Long): Int {
        var currentLevel = 1
        var xpRequiredForNext = currentLevel * BASE_XP_PER_LEVEL
        var remainingXp = totalXp

        while (remainingXp >= xpRequiredForNext) {
            remainingXp -= xpRequiredForNext
            currentLevel++
            xpRequiredForNext = currentLevel * BASE_XP_PER_LEVEL
        }

        return currentLevel
    }

    /**
     * Computes the cumulative XP threshold required to reach a specific level.
     */
    fun totalXpThresholdForLevel(targetLevel: Int): Long {
        var accumulated = 0L
        for (lvl in 1 until targetLevel) {
            accumulated += lvl * BASE_XP_PER_LEVEL
        }
        return accumulated
    }

    /**
     * Evaluates a completed match and awards XP, coins, level upgrades, and cosmetic unlocks.
     */
    fun applyMatchOutcome(
        profile: LocalProfile,
        placement: Int,
        tokensCaptured: Int,
        isWin: Boolean
    ): Pair<LocalProfile, MatchReward> {
        val xpGain = when (placement) {
            1 -> 100L
            2 -> 60L
            3 -> 35L
            else -> 20L
        } + (tokensCaptured * 15L)

        val coinGain = when (placement) {
            1 -> 50L
            2 -> 30L
            3 -> 15L
            else -> 10L
        } + (tokensCaptured * 5L)

        val oldLevel = profile.level
        val newTotalXp = profile.experiencePoints + xpGain
        val newLevel = calculateLevel(newTotalXp)
        val isLevelUp = newLevel > oldLevel

        // Check if any cosmetic item unlocks at this new level
        val newlyUnlockedCosmetic = if (isLevelUp) {
            CosmeticItem.ALL_COSMETICS.firstOrNull { it.unlockLevel in (oldLevel + 1)..newLevel }
        } else null

        val updatedUnlockedList = if (newlyUnlockedCosmetic != null && !profile.unlockedCosmeticIds.contains(newlyUnlockedCosmetic.id)) {
            profile.unlockedCosmeticIds + newlyUnlockedCosmetic.id
        } else {
            profile.unlockedCosmeticIds
        }

        val updatedProfile = profile.copy(
            experiencePoints = newTotalXp,
            level = newLevel,
            coins = profile.coins + coinGain,
            statsMatchesPlayed = profile.statsMatchesPlayed + 1,
            statsMatchesWon = profile.statsMatchesWon + (if (isWin) 1 else 0),
            statsTokensCaptured = profile.statsTokensCaptured + tokensCaptured,
            unlockedCosmeticIds = updatedUnlockedList,
            lastActiveTimestamp = System.currentTimeMillis()
        )

        val reward = MatchReward(
            xpEarned = xpGain,
            coinsEarned = coinGain,
            isLevelUp = isLevelUp,
            oldLevel = oldLevel,
            newLevel = newLevel,
            placement = placement,
            captures = tokensCaptured,
            unlockedCosmetic = newlyUnlockedCosmetic
        )

        return updatedProfile to reward
    }

    /**
     * Unlocks a cosmetic item by deducting coin price if prerequisites are met.
     */
    fun purchaseCosmetic(profile: LocalProfile, cosmetic: CosmeticItem): LocalProfile {
        if (profile.unlockedCosmeticIds.contains(cosmetic.id)) return profile
        if (profile.coins < cosmetic.coinPrice || profile.level < cosmetic.unlockLevel) return profile

        return profile.copy(
            coins = profile.coins - cosmetic.coinPrice,
            unlockedCosmeticIds = profile.unlockedCosmeticIds + cosmetic.id
        )
    }

    /**
     * Equips an unlocked cosmetic item to the active profile.
     */
    fun equipCosmetic(profile: LocalProfile, cosmetic: CosmeticItem): LocalProfile {
        if (!profile.unlockedCosmeticIds.contains(cosmetic.id)) return profile

        return when (cosmetic.category) {
            CosmeticCategory.DICE_SKIN -> profile.copy(equippedDiceSkinId = cosmetic.id)
            CosmeticCategory.TOKEN_STYLE -> profile.copy(equippedTokenStyleId = cosmetic.id)
            CosmeticCategory.BOARD_THEME -> profile.copy(equippedBoardThemeId = cosmetic.id)
        }
    }
}
