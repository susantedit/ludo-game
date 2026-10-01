package game.ludora.engine.ludo.model

import game.ludora.core.model.PlayerColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LudoBoardTest {

    @Test
    fun testStartStepsForColors() {
        assertEquals(0, LudoBoard.startStep(PlayerColor.RED))
        assertEquals(13, LudoBoard.startStep(PlayerColor.GREEN))
        assertEquals(26, LudoBoard.startStep(PlayerColor.YELLOW))
        assertEquals(39, LudoBoard.startStep(PlayerColor.BLUE))
    }

    @Test
    fun testHomeEntranceStepsForColors() {
        assertEquals(50, LudoBoard.homeEntranceStep(PlayerColor.RED))
        assertEquals(11, LudoBoard.homeEntranceStep(PlayerColor.GREEN))
        assertEquals(24, LudoBoard.homeEntranceStep(PlayerColor.YELLOW))
        assertEquals(37, LudoBoard.homeEntranceStep(PlayerColor.BLUE))
    }

    @Test
    fun testSafeSquares() {
        assertEquals(8, LudoBoard.SAFE_SQUARES.size)
        assertTrue(LudoBoard.isSafeSquare(0))
        assertTrue(LudoBoard.isSafeSquare(8))
        assertTrue(LudoBoard.isSafeSquare(13))
        assertTrue(LudoBoard.isSafeSquare(21))
        assertTrue(LudoBoard.isSafeSquare(26))
        assertTrue(LudoBoard.isSafeSquare(34))
        assertTrue(LudoBoard.isSafeSquare(39))
        assertTrue(LudoBoard.isSafeSquare(47))

        assertFalse(LudoBoard.isSafeSquare(1))
        assertFalse(LudoBoard.isSafeSquare(7))
        assertFalse(LudoBoard.isSafeSquare(51))
    }

    @Test
    fun testReleaseFromBase() {
        val basePos = LudoPosition.InBase(slotIndex = 0)
        
        // Non-6 rolls should return null
        for (roll in 1..5) {
            assertNull(LudoBoard.calculateNextPosition(basePos, roll, PlayerColor.RED))
        }

        // Roll 6 releases to start step
        val nextRed = LudoBoard.calculateNextPosition(basePos, 6, PlayerColor.RED)
        assertEquals(LudoPosition.OnTrack(0), nextRed)

        val nextGreen = LudoBoard.calculateNextPosition(basePos, 6, PlayerColor.GREEN)
        assertEquals(LudoPosition.OnTrack(13), nextGreen)

        val nextYellow = LudoBoard.calculateNextPosition(basePos, 6, PlayerColor.YELLOW)
        assertEquals(LudoPosition.OnTrack(26), nextYellow)

        val nextBlue = LudoBoard.calculateNextPosition(basePos, 6, PlayerColor.BLUE)
        assertEquals(LudoPosition.OnTrack(39), nextBlue)
    }

    @Test
    fun testTrackProgressionBeforeEntrance() {
        // Red token at step 0 rolls 4 -> step 4
        val pos = LudoPosition.OnTrack(0)
        val next = LudoBoard.calculateNextPosition(pos, 4, PlayerColor.RED)
        assertEquals(LudoPosition.OnTrack(4), next)

        // Green token at step 13 rolls 6 -> step 19
        val greenPos = LudoPosition.OnTrack(13)
        val nextGreen = LudoBoard.calculateNextPosition(greenPos, 6, PlayerColor.GREEN)
        assertEquals(LudoPosition.OnTrack(19), nextGreen)
    }

    @Test
    fun testHomePathTransition() {
        // Red entrance is 50. Token at 48 rolls 4:
        // 48 -> 49 (1), 49 -> 50 (2), 50 -> H1 (3), H1 -> H2 (4)
        val pos = LudoPosition.OnTrack(48)
        val next = LudoBoard.calculateNextPosition(pos, 4, PlayerColor.RED)
        assertEquals(LudoPosition.InHomePath(2), next)

        // Exactly reaching entrance: Red at 48 rolls 2 -> step 50
        val exactEntrance = LudoBoard.calculateNextPosition(pos, 2, PlayerColor.RED)
        assertEquals(LudoPosition.OnTrack(50), exactEntrance)

        // Entering H1: Red at 50 rolls 1 -> H1
        val entrancePos = LudoPosition.OnTrack(50)
        val h1 = LudoBoard.calculateNextPosition(entrancePos, 1, PlayerColor.RED)
        assertEquals(LudoPosition.InHomePath(1), h1)
    }

    @Test
    fun testDirectFinishFromTrack() {
        // Red at step 50 rolls 6:
        // 50 -> H1 (1), H2 (2), H3 (3), H4 (4), H5 (5), Finished (6)
        val entrancePos = LudoPosition.OnTrack(50)
        val finished = LudoBoard.calculateNextPosition(entrancePos, 6, PlayerColor.RED)
        assertEquals(LudoPosition.Finished, finished)
    }

    @Test
    fun testHomePathProgressionAndExactFinish() {
        val h2 = LudoPosition.InHomePath(2)
        
        // H2 + 2 -> H4
        assertEquals(LudoPosition.InHomePath(4), LudoBoard.calculateNextPosition(h2, 2, PlayerColor.RED))

        // H2 + 4 -> Finished (exact roll: 6 - 2 = 4)
        assertEquals(LudoPosition.Finished, LudoBoard.calculateNextPosition(h2, 4, PlayerColor.RED))

        // H2 + 5 -> Overshoot (null)
        assertNull(LudoBoard.calculateNextPosition(h2, 5, PlayerColor.RED))

        val h5 = LudoPosition.InHomePath(5)
        // H5 + 1 -> Finished
        assertEquals(LudoPosition.Finished, LudoBoard.calculateNextPosition(h5, 1, PlayerColor.RED))

        // H5 + 2 -> Overshoot (null)
        assertNull(LudoBoard.calculateNextPosition(h5, 2, PlayerColor.RED))
    }

    @Test
    fun testGreenTrackWrappingAround51() {
        // Green starts at 13. Entrance is 11.
        // At step 50 rolls 4 -> (50 + 4) % 52 = step 2
        val greenOn50 = LudoPosition.OnTrack(50)
        val next = LudoBoard.calculateNextPosition(greenOn50, 4, PlayerColor.GREEN)
        assertEquals(LudoPosition.OnTrack(2), next)

        // Green at step 9 rolls 4:
        // 9 -> 10 (1), 10 -> 11 (2), 11 -> H1 (3), H1 -> H2 (4)
        val greenAt9 = LudoPosition.OnTrack(9)
        val nextHome = LudoBoard.calculateNextPosition(greenAt9, 4, PlayerColor.GREEN)
        assertEquals(LudoPosition.InHomePath(2), nextHome)
    }

    @Test
    fun testDistanceToGoal() {
        assertEquals(57, LudoBoard.distanceToGoal(LudoPosition.InBase(0), PlayerColor.RED))
        assertEquals(56, LudoBoard.distanceToGoal(LudoPosition.OnTrack(0), PlayerColor.RED))
        assertEquals(6, LudoBoard.distanceToGoal(LudoPosition.OnTrack(50), PlayerColor.RED))
        assertEquals(5, LudoBoard.distanceToGoal(LudoPosition.InHomePath(1), PlayerColor.RED))
        assertEquals(1, LudoBoard.distanceToGoal(LudoPosition.InHomePath(5), PlayerColor.RED))
        assertEquals(0, LudoBoard.distanceToGoal(LudoPosition.Finished, PlayerColor.RED))
    }
}
