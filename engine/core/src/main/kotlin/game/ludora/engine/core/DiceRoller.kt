package game.ludora.engine.core

import java.security.SecureRandom

interface DiceRoller {
    fun roll(): Int
}

class SecureDiceRoller(
    private val random: SecureRandom = SecureRandom()
) : DiceRoller {
    override fun roll(): Int {
        // Uniform discrete integer in [1, 6]
        return random.nextInt(6) + 1
    }
}

class DeterministicDiceRoller(
    private val rollSequence: List<Int>
) : DiceRoller {
    private var index = 0

    override fun roll(): Int {
        if (rollSequence.isEmpty()) return 1
        val result = rollSequence[index % rollSequence.size]
        index++
        return result
    }
}
