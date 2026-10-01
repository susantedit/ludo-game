package game.ludora.core.network.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomCodeGeneratorTest {

    @Test
    fun testGeneratedCodeLengthAndCharset() {
        val code = RoomCodeGenerator.generate()
        assertEquals(6, code.length)
        assertTrue(RoomCodeGenerator.isValid(code))
    }

    @Test
    fun testExcludesConfusingCharacters() {
        val confusing = setOf('0', 'O', '1', 'I')
        for (i in 1..500) {
            val code = RoomCodeGenerator.generate()
            for (char in code) {
                assertFalse("Room code must not contain confusing char $char", confusing.contains(char))
            }
        }
    }

    @Test
    fun testValidationLogic() {
        assertTrue(RoomCodeGenerator.isValid("K7X92P"))
        assertTrue(RoomCodeGenerator.isValid("234567"))
        assertFalse(RoomCodeGenerator.isValid("K7X92")) // too short
        assertFalse(RoomCodeGenerator.isValid("K7X92PP")) // too long
        assertFalse(RoomCodeGenerator.isValid("K7X92O")) // contains O
        assertFalse(RoomCodeGenerator.isValid("K7X921")) // contains 1
        assertFalse(RoomCodeGenerator.isValid("K7X92I")) // contains I
        assertFalse(RoomCodeGenerator.isValid("K7X920")) // contains 0
    }

    @Test
    fun testUniquenessDistribution() {
        val generated = mutableSetOf<String>()
        for (i in 1..1000) {
            val code = RoomCodeGenerator.generate()
            generated.add(code)
        }
        // With 32^6 = ~1.07 billion possibilities, 1000 codes should all be unique
        assertEquals(1000, generated.size)
    }
}
