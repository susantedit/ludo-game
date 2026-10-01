package game.ludora.core.network.model

import game.ludora.core.model.GameType
import game.ludora.core.model.PlayerColor
import kotlinx.serialization.Serializable

@Serializable
enum class RoomStatus {
    LOBBY,
    STARTING,
    IN_PROGRESS,
    COMPLETED
}

@Serializable
data class RoomPlayer(
    val id: String,
    val displayName: String,
    val seatIndex: Int,
    val color: PlayerColor,
    val isReady: Boolean = false,
    val isHost: Boolean = false,
    val isConnected: Boolean = true
)

@Serializable
data class RoomConfig(
    val gameType: GameType = GameType.LUDO,
    val maxPlayers: Int = 4,
    val turnTimeoutSeconds: Int = 20,
    val isPrivate: Boolean = true
)

@Serializable
data class RoomDetails(
    val roomId: String,
    val roomCode: String,
    val status: RoomStatus = RoomStatus.LOBBY,
    val config: RoomConfig = RoomConfig(),
    val players: List<RoomPlayer> = emptyList(),
    val createdAtTimestamp: Long = System.currentTimeMillis()
) {
    val isFull: Boolean get() = players.size >= config.maxPlayers
    val canStart: Boolean get() = players.size >= 2 && players.all { it.isReady || it.isHost }
}
