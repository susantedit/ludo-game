package game.ludora.engine.snake

import game.ludora.core.model.PlayerColor
import game.ludora.engine.core.EngineAction
import game.ludora.engine.snake.model.SnakeAction
import game.ludora.engine.snake.model.SnakeTurnPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SnakeGameEngineTest {

    @Test
    fun testInitialStateTwoPlayers() {
        val engine = SnakeGameEngine()
        val state = engine.getInitialState(playerCount = 2, seed = 42L)

        assertEquals(2, state.players.size)
        assertEquals(PlayerColor.RED, state.players[0].color)
        assertEquals(PlayerColor.YELLOW, state.players[1].color)
        assertEquals(0, state.activeSeatIndex)
        assertEquals(SnakeTurnPhase.WAITING_FOR_ROLL, state.phase)
        assertFalse(state.isGameOver)
        assertEquals(0, state.players[0].currentSquare)
        assertEquals(0, state.players[1].currentSquare)
    }

    @Test
    fun testLegalMovesReturnsSingleToken() {
        val engine = SnakeGameEngine()
        val state = engine.getInitialState(playerCount = 2, seed = 42L)
        val tokens = engine.computeLegalMoves(state, 5)

        assertEquals(1, tokens.size)
        assertEquals("snake_token_0", tokens[0].id)
        assertEquals(PlayerColor.RED, tokens[0].color)
    }

    @Test
    fun testEngineReduceRollDice() {
        val engine = SnakeGameEngine()
        val initial = engine.getInitialState(playerCount = 2, seed = 42L)
        val result = engine.reduce(initial, EngineAction.RollDice(0), rollValue = 4)

        // Square 4 is a ladder base (4 -> 14)
        assertEquals(14, result.state.players[0].currentSquare)
        assertEquals(1, result.state.activeSeatIndex)
        assertTrue(result.events.isNotEmpty())
    }

    @Test
    fun testHeadless100MatchesSimulation() {
        val random = Random(12345)
        val engine = SnakeGameEngine()

        for (matchIndex in 1..100) {
            var state = engine.getInitialState(playerCount = 4, seed = matchIndex.toLong())
            var movesCount = 0

            while (!state.isGameOver && movesCount < 2000) {
                val roll = random.nextInt(1, 7)
                val (nextState, _) = engine.step(state, SnakeAction.RollDice(forcedValue = roll))
                state = nextState
                movesCount++

                for (p in state.players) {
                    assertTrue("Player ${p.color} square must be between 0 and 100", p.currentSquare in 0..100)
                }
            }

            assertTrue("Match $matchIndex must terminate", state.isGameOver)
            assertTrue("Match $matchIndex must have at least one winner", state.winners.isNotEmpty())
            assertTrue("Round count must not exceed 200", state.roundCount <= 200)
        }
    }
}
