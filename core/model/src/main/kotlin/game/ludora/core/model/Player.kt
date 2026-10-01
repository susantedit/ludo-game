package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class AiDifficulty {
    EASY,
    MEDIUM,
    HARD
}

@Serializable
enum class ConnectionStatus {
    CONNECTED,
    RECONNECTING,
    DISCONNECTED
}

@Serializable
data class Player(
    val id: String,
    val displayName: String,
    val seatIndex: Int,
    val color: PlayerColor,
    val isAi: Boolean = false,
    val aiDifficulty: AiDifficulty? = null,
    val isHost: Boolean = false,
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTED
)
