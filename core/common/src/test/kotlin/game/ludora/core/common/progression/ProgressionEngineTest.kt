package game.ludora.core.common.progression

import game.ludora.core.model.CosmeticItem
import game.ludora.core.model.DailyQuest
import game.ludora.core.model.LocalProfile
import game.ludora.core.model.QuestType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionEngineTest {

    @Test
    fun testLevelCalculation() {
        assertEquals(1, ProgressionEngine.calculateLevel(0L))
        assertEquals(1, ProgressionEngine.calculateLevel(149L))
        assertEquals(2, ProgressionEngine.calculateLevel(150L))
        assertEquals(2, ProgressionEngine.calculateLevel(449L))
        assertEquals(3, ProgressionEngine.calculateLevel(450L)) // 150 + 300
    }

    @Test
    fun testApplyMatchOutcomeVictory() {
        val initialProfile = LocalProfile(
            profileId = "test_player",
            experiencePoints = 0L,
            level = 1,
            coins = 100L,
            statsMatchesPlayed = 0,
            statsMatchesWon = 0,
            statsTokensCaptured = 0
        )

        // 1st place victory with 2 token captures
        val (updatedProfile, reward) = ProgressionEngine.applyMatchOutcome(
            profile = initialProfile,
            placement = 1,
            tokensCaptured = 2,
            isWin = true
        )

        // 100 base + 2*15 = 130 XP
        assertEquals(130L, reward.xpEarned)
        // 50 base + 2*5 = 60 coins
        assertEquals(60L, reward.coinsEarned)
        assertEquals(160L, updatedProfile.coins)
        assertEquals(130L, updatedProfile.experiencePoints)
        assertEquals(1, updatedProfile.level)
        assertFalse(reward.isLevelUp)

        assertEquals(1, updatedProfile.statsMatchesPlayed)
        assertEquals(1, updatedProfile.statsMatchesWon)
        assertEquals(2, updatedProfile.statsTokensCaptured)
        assertEquals(100f, updatedProfile.winRate, 0.01f)
    }

    @Test
    fun testLevelUpMilestoneReward() {
        val initialProfile = LocalProfile(
            profileId = "test_player",
            experiencePoints = 120L,
            level = 1,
            coins = 100L
        )

        // 1st place victory earns 100 XP -> Total 220 XP (crosses 150 threshold to Level 2!)
        val (updatedProfile, reward) = ProgressionEngine.applyMatchOutcome(
            profile = initialProfile,
            placement = 1,
            tokensCaptured = 0,
            isWin = true
        )

        assertTrue(reward.isLevelUp)
        assertEquals(1, reward.oldLevel)
        assertEquals(2, reward.newLevel)
        assertEquals(2, updatedProfile.level)
        // Level 2 unlocks Golden Ember dice skin!
        assertNotNull(reward.unlockedCosmetic)
        assertEquals("dice_golden_ember", reward.unlockedCosmetic!!.id)
        assertTrue(updatedProfile.unlockedCosmeticIds.contains("dice_golden_ember"))
    }

    @Test
    fun testPurchaseAndEquipCosmetics() {
        val initialProfile = LocalProfile(
            profileId = "test_player",
            level = 4,
            coins = 500L,
            unlockedCosmeticIds = listOf("dice_classic", "token_classic", "board_obsidian")
        )

        val woodBoard = CosmeticItem.BOARD_WARM_WOOD // Level 4, 300 coins
        val purchasedProfile = ProgressionEngine.purchaseCosmetic(initialProfile, woodBoard)
        assertEquals(200L, purchasedProfile.coins)
        assertTrue(purchasedProfile.unlockedCosmeticIds.contains(woodBoard.id))

        val equippedProfile = ProgressionEngine.equipCosmetic(purchasedProfile, woodBoard)
        assertEquals(woodBoard.id, equippedProfile.equippedBoardThemeId)
    }

    @Test
    fun testDailyQuestProgressAndClaim() {
        val initialQuests = DailyQuest.defaultQuests()
        // Record match win with 2 captures and 3 sixes
        val updatedQuests = DailyQuestEngine.recordMatchEvents(
            quests = initialQuests,
            isWin = true,
            tokensCaptured = 2,
            sixesRolled = 3
        )

        val winQuest = updatedQuests.first { it.type == QuestType.WIN_MATCH }
        assertTrue("Win match quest should be completed", winQuest.isCompleted)
        assertFalse(winQuest.isClaimed)

        val profile = LocalProfile(
            profileId = "quest_tester",
            experiencePoints = 0L,
            coins = 50L,
            activeQuests = updatedQuests
        )

        val (claimedProfile, claimedQuest) = DailyQuestEngine.claimQuest(profile, winQuest.id)
        assertNotNull(claimedQuest)
        assertTrue(claimedQuest!!.isClaimed)
        // 50 coins + 50 quest reward = 100 coins
        assertEquals(100L, claimedProfile.coins)
        // 80 XP awarded
        assertEquals(80L, claimedProfile.experiencePoints)
    }
}
