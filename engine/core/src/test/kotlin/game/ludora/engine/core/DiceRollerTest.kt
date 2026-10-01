package game.ludora.engine.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceRollerTest {

    @Test
    fun testSecureDiceRollerOutputsValuesBetweenOneAndSix() {
        val roller = SecureDiceRoller()
        val counts = mutableMapOf<Int, Int>()

        for (i in 1..10000) {
            val roll = roller.roll()
            assertTrue("Roll must be >= 1", roll >= 1)
            assertTrue("Roll must be <= 6", roll <= 6)
            counts[roll] = (counts[roll] ?: 0) + 1
        }

        // Verify each face was rolled
        for (face in 1..6) {
            assertTrue("Face $face should have been rolled", (counts[face] ?: 0) > 1000)
        }
    }

    @Test
    fun testDeterministicDiceRollerRepeatsSequence() {
        val sequence = listOf(6, 6, 2, 4, 1)
        val roller = DeterministicDiceRoller(sequence)

        for (expected in sequence) {
            assertEquals(expected, roller.roll())
        }
        // Loops back to start
        assertEquals(sequence[0], roller.roll())
    }
}
