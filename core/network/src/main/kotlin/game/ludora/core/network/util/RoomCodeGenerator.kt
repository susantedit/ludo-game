package game.ludora.core.network.util

import java.security.SecureRandom

/**
 * Generates unambiguous 6-character room codes for online matchmaking and private lobbies.
 * Uses a modified Crockford base-32 alphabet that excludes easily confused glyphs (0, O, 1, I).
 */
object RoomCodeGenerator {

    private const val CODE_LENGTH = 6
    private const val ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
    private val random = SecureRandom()

    fun generate(): String {
        val chars = CharArray(CODE_LENGTH)
        for (i in 0 until CODE_LENGTH) {
            val index = random.nextInt(ALPHABET.length)
            chars[i] = ALPHABET[index]
        }
        return String(chars)
    }

    fun isValid(code: String): Boolean {
        if (code.length != CODE_LENGTH) return false
        val upper = code.uppercase()
        return upper.all { ALPHABET.contains(it) }
    }
}
