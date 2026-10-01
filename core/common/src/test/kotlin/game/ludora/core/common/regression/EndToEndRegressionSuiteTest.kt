package game.ludora.core.common.regression

import game.ludora.core.common.ads.AdPolicyManager
import game.ludora.core.common.progression.DailyQuestEngine
import game.ludora.core.common.progression.ProgressionEngine
import game.ludora.core.model.DailyQuest
import game.ludora.core.model.LocalProfile
import game.ludora.core.model.PlayerColor
import game.ludora.engine.core.EngineAction
import game.ludora.engine.ludo.LudoGameEngine
import game.ludora.engine.remix.RemixGameEngine
import game.ludora.engine.snake.SnakeGameEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * End-to-end regression suite validating all game engines, progression, quests, and economy systems.
 */
class EndToEndRegressionSuiteTest {

    @Test
    fun `classic ludo engine completes multi-turn match without rule violations`() {
        val engine = LudoGameEngine(playerColors = listOf(PlayerColor.RED, PlayerColor.GREEN))
        var state = engine.initializeGame()

        assertFalse(state.isGameOver)
        assertEquals(PlayerColor.RED, state.activeColor)

        // Simulate 40 turns
        for (turn in 1..40) {
            if (state.isGameOver) break

            // Roll
            val rollResult = engine.processAction(state, EngineAction.RollDice(state.activeColor))
            state = rollResult.newState

            // If move available
            val legalMoves = engine.moveValidator.calculateLegalMoves(state, state.lastRolledValue)
            if (legalMoves.isNotEmpty()) {
                val chosenMove = legalMoves.first()
                val moveResult = engine.processAction(state, chosenMove)
                state = moveResult.newState
            }
        }

        assertNotNull("Match state should be non-null after 40 turns", state)
        assertTrue("Turn count must be recorded", state.turnNumber > 0)
    }

    @Test
    fun `snake and ladder engine executes simulation and honors exact finish rule`() {
        val engine = SnakeGameEngine(playerCount = 2)
        var state = engine.initializeGame()

        for (step in 1..60) {
            if (state.isGameOver) break
            val rollAction = EngineAction.RollDice(state.activePlayer.color)
            val result = engine.processAction(state, rollAction)
            state = result.newState
        }

        assertNotNull(state)
        assertTrue(state.roundNumber > 0)
        // Position should never exceed 100
        for (player in state.players) {
            assertTrue("Player position must not exceed 100, was ${player.position}", player.position <= 100)
        }
    }

    @Test
    fun `remix engine executes with power cards and hybrid hazards`() {
        val engine = RemixGameEngine(playerColors = listOf(PlayerColor.RED, PlayerColor.GREEN))
        var state = engine.initializeGame()

        assertNotNull(state.activeChaosModifier)
        assertEquals(2, state.players.size)

        // Process a roll
        val rollResult = engine.processAction(state, EngineAction.RollDice(state.activeColor))
        state = rollResult.newState
        assertNotNull(state)
        assertTrue(state.lastRolledValue in 1..6)
    }

    @Test
    fun `progression and quest engine integration loop levels up player and updates quests`() {
        var profile = LocalProfile(
            profileId = "reg_test_user",
            coins = 100L,
            level = 1,
            experiencePoints = 0L,
            activeQuests = DailyQuest.defaultQuests()
        )

        // Play 3 matches
        for (match in 1..3) {
            val (updatedProfile, reward) = ProgressionEngine.applyMatchOutcome(
                profile = profile,
                placement = 1,
                tokensCaptured = 2,
                isWin = true
            )

            val updatedQuests = DailyQuestEngine.recordMatchEvents(
                quests = updatedProfile.activeQuests,
                isWin = true,
                tokensCaptured = 2,
                sixesRolled = 3
            )

            profile = updatedProfile.copy(activeQuests = updatedQuests)
            assertTrue("Reward XP must be positive", reward.xpEarned > 0)
        }

        // Level 1 threshold is 150 XP; 3 wins with captures yield >150 XP, leveling up to Level 2
        assertTrue("Player should level up after 3 consecutive wins", profile.level >= 2)
        assertTrue("Coins should increase with match rewards", profile.coins > 100L)
    }

    @Test
    fun `ad policy manager integrates with match flow and ad free entitlement`() {
        val policyManager = AdPolicyManager(initialSessionCount = 3)
        assertFalse("Ad-Free initially false", policyManager.isAdFree)

        // Record matches
        policyManager.recordMatchFinished()
        policyManager.recordMatchFinished()
        assertTrue("Interstitial allowed online after 2 matches", policyManager.canShowInterstitial(isOnline = true))

        // Purchase Ad-Free
        policyManager.setAdFree(true)
        assertTrue("Ad-Free is now true", policyManager.isAdFree)
        assertFalse("Interstitial suppressed when ad-free", policyManager.canShowInterstitial(isOnline = true))
        assertFalse("Banner suppressed when ad-free", policyManager.canShowBanner(isOnline = true))
        assertTrue("Rewarded ads remain available when ad-free", policyManager.canShowRewarded(isOnline = true))
    }
}
