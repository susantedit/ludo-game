package game.ludora.core.network.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ClientPacket {
    @Serializable
    data class Ping(val timestamp: Long = System.currentTimeMillis()) : ClientPacket

    @Serializable
    data class JoinRoom(val roomCode: String) : ClientPacket

    @Serializable
    data class ToggleReady(val isReady: Boolean) : ClientPacket

    @Serializable
    data class StartMatch(val roomId: String) : ClientPacket

    @Serializable
    data class SendRollIntent(val roomId: String, val seatIndex: Int) : ClientPacket

    @Serializable
    data class SendMoveIntent(val roomId: String, val seatIndex: Int, val tokenId: Int) : ClientPacket

    @Serializable
    data class LeaveRoom(val roomId: String) : ClientPacket
}

@Serializable
sealed interface ServerPacket {
    @Serializable
    data class Pong(val originalTimestamp: Long, val serverTimestamp: Long = System.currentTimeMillis()) : ServerPacket

    @Serializable
    data class RoomUpdated(val room: RoomDetails) : ServerPacket

    @Serializable
    data class MatchStarted(val roomId: String, val firstSeatIndex: Int, val turnDeadlineTimestamp: Long) : ServerPacket

    @Serializable
    data class TurnBegan(val activeSeatIndex: Int, val turnDeadlineTimestamp: Long) : ServerPacket

    @Serializable
    data class ActionBroadcast(val seatIndex: Int, val actionType: String, val payloadJson: String) : ServerPacket

    @Serializable
    data class PlayerDisconnected(val seatIndex: Int, val gracePeriodSeconds: Int = 60) : ServerPacket

    @Serializable
    data class PlayerReconnected(val seatIndex: Int) : ServerPacket

    @Serializable
    data class Error(val code: String, val message: String) : ServerPacket
}
