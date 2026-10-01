package game.ludora.engine.remix

import game.ludora.core.model.PlayerColor
import game.ludora.core.model.TokenState
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.remix.model.PowerUpType
import game.ludora.engine.remix.model.RemixConfig
import org.junit.Assert.assertEquals
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
    fun testSpeedBoostModifier() {
        val engine = RemixGameEngine()
        val initial = engine.getInitialState(playerCount = 2)

        val boostState = engine.activatePowerUp(
            state = initial,
            playerColor = PlayerColor.RED,
            powerUp = PowerUpType.SPEED_BOOST
        )

        assertEquals(2, boostState.activeRollModifier)
    }

    @Test
    fun testBombSendsAdjacentOpponentToBase() {
        val engine = RemixGameEngine()
        val initial = engine.getInitialState(playerCount = 2)

        // Setup: Red token 0 at step 10, Yellow token 0 at step 11
        val redPlayer = initial.baseState.players[0].copy(
            tokens = initial.baseState.players[0].tokens.mapIndexed { idx, t ->
                if (idx == 0) t.copy(position = LudoPosition.OnTrack(10), state = TokenState.ON_TRACK) else t
            }
        )
        val yellowPlayer = initial.baseState.players[1].copy(
            tokens = initial.baseState.players[1].tokens.mapIndexed { idx, t ->
                if (idx == 0) t.copy(position = LudoPosition.OnTrack(11), state = TokenState.ON_TRACK) else t
            }
        )

        val customState = initial.copy(
            baseState = initial.baseState.copy(players = listOf(redPlayer, yellowPlayer)),
            inventories = mapOf(PlayerColor.RED to listOf(PowerUpType.BOMB))
        )

        val bombedState = engine.activatePowerUp(
            state = customState,
            playerColor = PlayerColor.RED,
            powerUp = PowerUpType.BOMB,
            targetTokenId = 0
        )

        val updatedYellowToken = bombedState.baseState.players[1].tokens[0]
        assertEquals(TokenState.IN_BASE, updatedYellowToken.state)
        assertTrue(updatedYellowToken.position is LudoPosition.InBase)
    }
}
