package game.ludora.engine.remix.model

import kotlinx.serialization.Serializable

@Serializable
enum class PowerUpType {
    SHIELD,
    SPEED_BOOST,
    REROLL,
    SWAP,
    BOMB
}

@Serializable
data class PowerUp(
    val id: String,
    val type: PowerUpType,
    val description: String
)

@Serializable
data class RemixConfig(
    val tokenCountPerPlayer: Int = 2,
    val maxRounds: Int = 100,
    val enableHazards: Boolean = true,
    val enableChaosModifiers: Boolean = true,
    val allowedPowerUps: Set<PowerUpType> = setOf(
        PowerUpType.SHIELD,
        PowerUpType.SPEED_BOOST,
        PowerUpType.REROLL,
        PowerUpType.SWAP,
        PowerUpType.BOMB
    )
)
