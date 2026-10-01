package game.ludora.engine.snake.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeBoardTest {

    @Test
    fun testClassicLaddersAndSnakesCounts() {
        assertEquals(7, SnakeBoard.LADDERS.size)
        assertEquals(8, SnakeBoard.SNAKES.size)
        assertEquals(100, SnakeBoard.TOTAL_SQUARES)
    }

    @Test
    fun testStartFromSquareZero() {
        // From virtual square 0, roll 3 moves to square 3
        val t1 = SnakeBoard.calculateDestination(currentSquare = 0, roll = 3)
        assertEquals(3, t1.landedSquare)
        assertEquals(3, t1.finalSquare)
        assertFalse(t1.isLadder)
        assertFalse(t1.isSnake)
        assertFalse(t1.isOvershoot)

        // From virtual square 0, roll 4 lands on ladder 4 -> climbs to 14
        val t2 = SnakeBoard.calculateDestination(currentSquare = 0, roll = 4)
        assertEquals(4, t2.landedSquare)
        assertEquals(14, t2.finalSquare)
        assertTrue(t2.isLadder)
        assertFalse(t2.isSnake)
    }

    @Test
    fun testLadderAscents() {
        // Base 9 -> Top 31
        val t1 = SnakeBoard.calculateDestination(currentSquare = 6, roll = 3)
        assertEquals(9, t1.landedSquare)
        assertEquals(31, t1.finalSquare)
        assertTrue(t1.isLadder)

        // Base 28 -> Top 84
        val t2 = SnakeBoard.calculateDestination(currentSquare = 25, roll = 3)
        assertEquals(28, t2.landedSquare)
        assertEquals(84, t2.finalSquare)
        assertTrue(t2.isLadder)
    }

    @Test
    fun testSnakeDrops() {
        // Head 17 -> Tail 7
        val t1 = SnakeBoard.calculateDestination(currentSquare = 12, roll = 5)
        assertEquals(17, t1.landedSquare)
        assertEquals(7, t1.finalSquare)
        assertTrue(t1.isSnake)

        // Head 99 -> Tail 78
        val t2 = SnakeBoard.calculateDestination(currentSquare = 96, roll = 3)
        assertEquals(99, t2.landedSquare)
        assertEquals(78, t2.finalSquare)
        assertTrue(t2.isSnake)
    }

    @Test
    fun testExactFinishRequirementAndOvershootStall() {
        // Square 98 rolls 2 -> Exactly 100 (win)
        val win = SnakeBoard.calculateDestination(currentSquare = 98, roll = 2)
        assertEquals(100, win.landedSquare)
        assertEquals(100, win.finalSquare)
        assertFalse(win.isOvershoot)

        // Square 98 rolls 3 -> 101 > 100 (overshoot -> stalls at 98)
        val overshoot = SnakeBoard.calculateDestination(currentSquare = 98, roll = 3)
        assertEquals(98, overshoot.finalSquare)
        assertTrue(overshoot.isOvershoot)
    }

    @Test
    fun testBoustrophedonCoordinates() {
        // Row 0 (bottom): 1 (col 0) to 10 (col 9)
        assertEquals(Pair(0, 0), SnakeBoard.boustrophedonCoordinates(1))
        assertEquals(Pair(0, 9), SnakeBoard.boustrophedonCoordinates(10))

        // Row 1: 11 (col 9) to 20 (col 0)
        assertEquals(Pair(1, 9), SnakeBoard.boustrophedonCoordinates(11))
        assertEquals(Pair(1, 0), SnakeBoard.boustrophedonCoordinates(20))

        // Row 9 (top): 91 (col 9) to 100 (col 0)
        assertEquals(Pair(9, 9), SnakeBoard.boustrophedonCoordinates(91))
        assertEquals(Pair(9, 0), SnakeBoard.boustrophedonCoordinates(100))
    }
}
