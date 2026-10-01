package game.ludora.engine.remix

import game.ludora.core.model.PlayerColor
import game.ludora.core.model.TokenState
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoTurnPhase
import game.ludora.engine.remix.model.ChaosModifier
import game.ludora.engine.remix.model.PowerUpType
import game.ludora.engine.remix.model.RemixConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemixGameEngineTest {

    @Test
    fun testQuickMatchTokenCount() {
        val engine = RemixGameEngine()
        val state = engine.getInitialState(playerCount = 2, config = RemixConfig(tokenCountPerPlayer = 2))

        assertEquals(2, state.baseState.players.size)
        assertEquals(2, state.baseState.players[0].tokens.size)
        assertEquals(2, state.baseState.players[1].tokens.size)
    }

    @Test
    fun testShieldActivation() {
        val engine = RemixGameEngine()
        val initial = engine.getInitialState(playerCount = 2)

        val next = engine.activatePowerUp(
            state = initial,
            playerColor = PlayerColor.RED,
            powerUp = PowerUpType.SHIELD,
            targetTokenId = 0
        )

        assertTrue(next.shieldedTokens.contains(PlayerColor.RED to 0))
    }

    @Test
    fun testRerollPowerCardResetsPhase() {
        val engine = RemixGameEngine()
        val initial = engine.getInitialState(playerCount = 2)

        // Simulate Red rolling 1
        val rolled = engine.step(initial, LudoAction.RollDice(forcedValue = 1))
        // Activate Reroll card
        val rerolled = engine.activatePowerUp(
            state = rolled,
            playerColor = PlayerColor.RED,
            powerUp = PowerUpType.REROLL
        )

        assertEquals(LudoTurnPhase.WAITING_FOR_ROLL, rerolled.baseState.phase)
        assertNull(rerolled.baseState.currentRoll)
    }

    @Test
    fun testLadderOnTrackLeapsForward() {
        val engine = RemixGameEngine()
        val initial = engine.getInitialState(playerCount = 2)

        // Setup: Red token 0 at step 5. Rolling 1 lands on ladder step 6 -> climbs to 18!
        val redPlayer = initial.baseState.players[0].copy(
            tokens = initial.baseState.players[0].tokens.mapIndexed { idx, t ->
                if (idx == 0) t.copy(position = LudoPosition.OnTrack(5), state = TokenState.ON_TRACK) else t
            }
        )
        val customState = initial.copy(
            baseState = initial.baseState.copy(players = listOf(redPlayer, initial.baseState.players[1]))
        )

        val rolledState = engine.step(customState, LudoAction.RollDice(forcedValue = 1))
        val movedState = engine.step(rolledState, LudoAction.SelectMove(tokenId = 0))

        val finalToken = movedState.baseState.players[0].tokens[0]
        assertEquals(18, (finalToken.position as LudoPosition.OnTrack).stepIndex)
        assertTrue(movedState.lastTriggeredHazardDescription?.contains("Ladder") == true)
    }

    @Test
    fun testSnakeOnTrackDropsBackward() {
        val engine = RemixGameEngine()
        val initial = engine.getInitialState(playerCount = 2)

        // Setup: Red token 0 at step 14. Rolling 2 lands on snake step 16 -> drops to 4!
        val redPlayer = initial.baseState.players[0].copy(
            tokens = initial.baseState.players[0].tokens.mapIndexed { idx, t ->
                if (idx == 0) t.copy(position = LudoPosition.OnTrack(14), state = TokenState.ON_TRACK) else t
            }
        )
        val customState = initial.copy(
            baseState = initial.baseState.copy(players = listOf(redPlayer, initial.baseState.players[1]))
        )

        val rolledState = engine.step(customState, LudoAction.RollDice(forcedValue = 2))
        val movedState = engine.step(rolledState, LudoAction.SelectMove(tokenId = 0))

        val finalToken = movedState.baseState.players[0].tokens[0]
        assertEquals(4, (finalToken.position as LudoPosition.OnTrack).stepIndex)
        assertTrue(movedState.lastTriggeredHazardDescription?.contains("Snake") == true)
    }

    @Test
    fun testShieldDeflectsSnakeOnTrack() {
        val engine = RemixGameEngine()
        val initial = engine.getInitialState(playerCount = 2)

        // Setup: Red token 0 is shielded at step 14.
        val redPlayer = initial.baseState.players[0].copy(
            tokens = initial.baseState.players[0].tokens.mapIndexed { idx, t ->
                if (idx == 0) t.copy(position = LudoPosition.OnTrack(14), state = TokenState.ON_TRACK) else t
            }
        )
        val customState = initial.copy(
            baseState = initial.baseState.copy(players = listOf(redPlayer, initial.baseState.players[1])),
            shieldedTokens = setOf(PlayerColor.RED to 0)
        )

        val rolledState = engine.step(customState, LudoAction.RollDice(forcedValue = 2))
        val movedState = engine.step(rolledState, LudoAction.SelectMove(tokenId = 0))

        val finalToken = movedState.baseState.players[0].tokens[0]
        // Stays on step 16, shield deflected the snake drop!
        assertEquals(16, (finalToken.position as LudoPosition.OnTrack).stepIndex)
        // Shield is consumed
        assertTrue(movedState.shieldedTokens.isEmpty())
    }

    @Test
    fun testChaosModifierDoubleRoll() {
        val engine = RemixGameEngine()
        val initial = engine.getInitialState(playerCount = 2).copy(
            activeChaosModifier = ChaosModifier.DOUBLE_ROLL
        )

        val rolledState = engine.step(initial, LudoAction.RollDice(forcedValue = 3))
        // 3 + 2 = 5
        assertEquals(5, rolledState.baseState.currentRoll)
    }
}
