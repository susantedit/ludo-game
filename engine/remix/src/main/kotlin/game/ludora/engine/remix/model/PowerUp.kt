package game.ludora.engine.remix.model

import kotlinx.serialization.Serializable

@Serializable
enum class PowerUpType {
    SHIELD,
    SPEED_BOOST,
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
    val allowedPowerUps: Set<PowerUpType> = setOf(
        PowerUpType.SHIELD,
        PowerUpType.SPEED_BOOST,
        PowerUpType.SWAP,
        PowerUpType.BOMB
    )
)
