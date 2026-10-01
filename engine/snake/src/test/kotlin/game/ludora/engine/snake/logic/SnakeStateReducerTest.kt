package game.ludora.engine.snake.logic

import game.ludora.core.model.Player
import game.ludora.core.model.PlayerColor
import game.ludora.engine.snake.model.SnakeAction
import game.ludora.engine.snake.model.SnakeEvent
import game.ludora.engine.snake.model.SnakeGameState
import game.ludora.engine.snake.model.SnakePlayerState
import game.ludora.engine.snake.model.SnakeTurnPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeStateReducerTest {

    private fun createPlayer(seatIndex: Int, color: PlayerColor): Player =
        Player(
            id = "player_$seatIndex",
            displayName = "Player $color",
            color = color,
            seatIndex = seatIndex,
            isAi = false
        )

    private fun create2PlayerGame(): SnakeGameState {
        val red = SnakePlayerState.initial(createPlayer(0, PlayerColor.RED))
        val green = SnakePlayerState.initial(createPlayer(1, PlayerColor.GREEN))
        return SnakeGameState(
            matchId = "test_snake",
            players = listOf(red, green),
            activeSeatIndex = 0
        )
    }

    @Test
    fun testStartSquareZeroAdvancement() {
        val initial = create2PlayerGame()

        // Red rolls 3 from square 0 -> advances to square 3, turn passes to Green
        val (state, events) = SnakeStateReducer.reduce(initial, SnakeAction.RollDice(3))
        assertEquals(3, state.players.first { it.color == PlayerColor.RED }.currentSquare)
        assertEquals(1, state.activeSeatIndex) // Passed to Green
        assertTrue(events.any { it is SnakeEvent.TokenAdvanced && it.toSquare == 3 })
    }

    @Test
    fun testLadderClimbAscent() {
        val initial = create2PlayerGame()

        // Red rolls 4 from square 0 -> lands on ladder 4 -> climbs to 14!
        val (state, events) = SnakeStateReducer.reduce(initial, SnakeAction.RollDice(4))
        assertEquals(14, state.players.first { it.color == PlayerColor.RED }.currentSquare)
        assertTrue(events.any { it is SnakeEvent.LadderClimbed && it.baseSquare == 4 && it.topSquare == 14 })
    }

    @Test
    fun testSnakeDropDescent() {
        // Red is at square 12. Rolls 5 -> lands on snake head 17 -> drops to tail 7!
        val red = SnakePlayerState(createPlayer(0, PlayerColor.RED), currentSquare = 12)
        val green = SnakePlayerState.initial(createPlayer(1, PlayerColor.GREEN))
        val state = SnakeGameState("snake_drop", listOf(red, green), activeSeatIndex = 0)

        val (nextState, events) = SnakeStateReducer.reduce(state, SnakeAction.RollDice(5))
        assertEquals(7, nextState.players.first { it.color == PlayerColor.RED }.currentSquare)
        assertTrue(events.any { it is SnakeEvent.SnakeDropped && it.headSquare == 17 && it.tailSquare == 7 })
    }

    @Test
    fun testRollSixGrantsBonusRoll() {
        val initial = create2PlayerGame()

        // Red rolls 6 from square 0 -> advances to square 6, gets bonus roll (seat remains 0)
        val (state, events) = SnakeStateReducer.reduce(initial, SnakeAction.RollDice(6))
        assertEquals(6, state.activePlayer.currentSquare)
        assertEquals(0, state.activeSeatIndex) // Red rolls again!
        assertEquals(1, state.activePlayer.consecutiveSixes)
        assertTrue(events.any { it is SnakeEvent.BonusRollGranted })
    }

    @Test
    fun testThreeConsecutiveSixesPenalty() {
        val initial = create2PlayerGame()

        // Roll 1: 6
        val (s1, _) = SnakeStateReducer.reduce(initial, SnakeAction.RollDice(6))
        // Roll 2: 6
        val (s2, _) = SnakeStateReducer.reduce(s1, SnakeAction.RollDice(6))
        // Roll 3: 6 -> Penalty! Forfeits move, passes turn to Green (seat 1)
        val (s3, events) = SnakeStateReducer.reduce(s2, SnakeAction.RollDice(6))

        assertEquals(1, s3.activeSeatIndex) // Green's turn
        assertTrue(events.any { it is SnakeEvent.ThreeSixesPenalty })
    }

    @Test
    fun testOvershootStallAt100() {
        // Red at square 98 rolls 4 (98 + 4 = 102 > 100) -> Stalls at 98, turn passes to Green
        val red = SnakePlayerState(createPlayer(0, PlayerColor.RED), currentSquare = 98)
        val green = SnakePlayerState.initial(createPlayer(1, PlayerColor.GREEN))
        val state = SnakeGameState("overshoot", listOf(red, green), activeSeatIndex = 0)

        val (nextState, events) = SnakeStateReducer.reduce(state, SnakeAction.RollDice(4))
        assertEquals(98, nextState.players.first { it.color == PlayerColor.RED }.currentSquare)
        assertEquals(1, nextState.activeSeatIndex)
        assertTrue(events.any { it is SnakeEvent.OvershootStalled })
    }

    @Test
    fun testExactWinAt100() {
        // Red at square 98 rolls 2 -> exactly 100 -> Wins game!
        val red = SnakePlayerState(createPlayer(0, PlayerColor.RED), currentSquare = 98)
        val green = SnakePlayerState.initial(createPlayer(1, PlayerColor.GREEN))
        val state = SnakeGameState("win", listOf(red, green), activeSeatIndex = 0)

        val (nextState, events) = SnakeStateReducer.reduce(state, SnakeAction.RollDice(2))
        assertEquals(100, nextState.players.first { it.color == PlayerColor.RED }.currentSquare)
        assertTrue(nextState.isGameOver)
        assertEquals(listOf(PlayerColor.RED, PlayerColor.GREEN), nextState.winners)
        assertTrue(events.any { it is SnakeEvent.PlayerFinished && it.rank == 1 })
        assertTrue(events.any { it is SnakeEvent.MatchCompleted })
    }
}
