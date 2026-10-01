package game.ludora.core.network.gateway

import game.ludora.core.model.PlayerColor
import game.ludora.core.network.model.ClientPacket
import game.ludora.core.network.model.RoomConfig
import game.ludora.core.network.model.RoomDetails
import game.ludora.core.network.model.RoomPlayer
import game.ludora.core.network.model.RoomStatus
import game.ludora.core.network.model.ServerPacket
import game.ludora.core.network.util.RoomCodeGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID

interface RealtimeGateway {
    fun observeEvents(userId: String): Flow<ServerPacket>
    suspend fun sendPacket(userId: String, packet: ClientPacket)
    suspend fun createRoom(hostUserId: String, hostDisplayName: String, config: RoomConfig = RoomConfig()): RoomDetails
    suspend fun joinRoom(userId: String, displayName: String, roomCode: String): Result<RoomDetails>
    suspend fun toggleReady(userId: String, roomId: String, isReady: Boolean): Result<RoomDetails>
    suspend fun startMatch(hostUserId: String, roomId: String): Result<RoomDetails>
    suspend fun disconnect(userId: String)
}

/**
 * Server-authoritative in-memory implementation of the RealtimeGateway.
 * Allows complete end-to-end integration and multiplayer testing without external network infrastructure.
 */
class InMemoryRealtimeGateway : RealtimeGateway {

    private val userChannels = mutableMapOf<String, MutableSharedFlow<ServerPacket>>()
    private val activeRooms = mutableMapOf<String, RoomDetails>() // roomId -> RoomDetails
    private val codeToRoomId = mutableMapOf<String, String>() // roomCode -> roomId
    private val playerToRoomId = mutableMapOf<String, String>() // userId -> roomId

    private fun getOrCreateChannel(userId: String): MutableSharedFlow<ServerPacket> {
        return userChannels.getOrPut(userId) {
            MutableSharedFlow(replay = 10, extraBufferCapacity = 64)
        }
    }

    override fun observeEvents(userId: String): Flow<ServerPacket> {
        return getOrCreateChannel(userId).asSharedFlow()
    }

    override suspend fun sendPacket(userId: String, packet: ClientPacket) {
        when (packet) {
            is ClientPacket.Ping -> {
                getOrCreateChannel(userId).emit(
                    ServerPacket.Pong(originalTimestamp = packet.timestamp)
                )
            }
            is ClientPacket.JoinRoom -> {
                joinRoom(userId, "Player", packet.roomCode)
            }
            is ClientPacket.ToggleReady -> {
                val roomId = playerToRoomId[userId] ?: return
                toggleReady(userId, roomId, packet.isReady)
            }
            is ClientPacket.StartMatch -> {
                startMatch(userId, packet.roomId)
            }
            is ClientPacket.SendRollIntent -> {
                broadcastToRoom(packet.roomId, ServerPacket.ActionBroadcast(
                    seatIndex = packet.seatIndex,
                    actionType = "ROLL_INTENT",
                    payloadJson = """{"seatIndex":${packet.seatIndex}}"""
                ))
            }
            is ClientPacket.SendMoveIntent -> {
                broadcastToRoom(packet.roomId, ServerPacket.ActionBroadcast(
                    seatIndex = packet.seatIndex,
                    actionType = "MOVE_INTENT",
                    payloadJson = """{"seatIndex":${packet.seatIndex},"tokenId":${packet.tokenId}}"""
                ))
            }
            is ClientPacket.LeaveRoom -> {
                disconnect(userId)
            }
        }
    }

    override suspend fun createRoom(
        hostUserId: String,
        hostDisplayName: String,
        config: RoomConfig
    ): RoomDetails {
        val roomId = "room_${UUID.randomUUID()}"
        var code = RoomCodeGenerator.generate()
        while (codeToRoomId.containsKey(code)) {
            code = RoomCodeGenerator.generate()
        }

        val hostPlayer = RoomPlayer(
            id = hostUserId,
            displayName = hostDisplayName,
            seatIndex = 0,
            color = PlayerColor.RED,
            isReady = true,
            isHost = true,
            isConnected = true
        )

        val room = RoomDetails(
            roomId = roomId,
            roomCode = code,
            status = RoomStatus.LOBBY,
            config = config,
            players = listOf(hostPlayer)
        )

        activeRooms[roomId] = room
        codeToRoomId[code] = roomId
        playerToRoomId[hostUserId] = roomId

        getOrCreateChannel(hostUserId).emit(ServerPacket.RoomUpdated(room))
        return room
    }

    override suspend fun joinRoom(userId: String, displayName: String, roomCode: String): Result<RoomDetails> {
        val cleanCode = roomCode.uppercase().trim()
        val roomId = codeToRoomId[cleanCode] ?: return Result.failure(IllegalArgumentException("Room code not found"))
        val room = activeRooms[roomId] ?: return Result.failure(IllegalStateException("Room inactive"))

        if (room.isFull) {
            getOrCreateChannel(userId).emit(ServerPacket.Error("ROOM_FULL", "This room has reached maximum players"))
            return Result.failure(IllegalStateException("Room is full"))
        }

        if (room.status != RoomStatus.LOBBY) {
            getOrCreateChannel(userId).emit(ServerPacket.Error("MATCH_IN_PROGRESS", "Match already started"))
            return Result.failure(IllegalStateException("Match already in progress"))
        }

        val existingPlayer = room.players.firstOrNull { it.id == userId }
        if (existingPlayer != null) {
            return Result.success(room)
        }

        val seatIndex = room.players.size
        val color = when (seatIndex) {
            0 -> PlayerColor.RED
            1 -> PlayerColor.GREEN
            2 -> PlayerColor.YELLOW
            3 -> PlayerColor.BLUE
            else -> PlayerColor.YELLOW
        }

        val newPlayer = RoomPlayer(
            id = userId,
            displayName = displayName,
            seatIndex = seatIndex,
            color = color,
            isReady = false,
            isHost = false,
            isConnected = true
        )

        val updatedRoom = room.copy(players = room.players + newPlayer)
        activeRooms[roomId] = updatedRoom
        playerToRoomId[userId] = roomId

        broadcastToRoom(roomId, ServerPacket.RoomUpdated(updatedRoom))
        return Result.success(updatedRoom)
    }

    override suspend fun toggleReady(userId: String, roomId: String, isReady: Boolean): Result<RoomDetails> {
        val room = activeRooms[roomId] ?: return Result.failure(IllegalStateException("Room not found"))
        val updatedPlayers = room.players.map {
            if (it.id == userId && !it.isHost) it.copy(isReady = isReady) else it
        }

        val updatedRoom = room.copy(players = updatedPlayers)
        activeRooms[roomId] = updatedRoom
        broadcastToRoom(roomId, ServerPacket.RoomUpdated(updatedRoom))
        return Result.success(updatedRoom)
    }

    override suspend fun startMatch(hostUserId: String, roomId: String): Result<RoomDetails> {
        val room = activeRooms[roomId] ?: return Result.failure(IllegalStateException("Room not found"))
        val host = room.players.firstOrNull { it.id == hostUserId && it.isHost }
            ?: return Result.failure(IllegalStateException("Only the room host can start the match"))

        if (!room.canStart) {
            return Result.failure(IllegalStateException("All players must be ready to start"))
        }

        val startedRoom = room.copy(status = RoomStatus.IN_PROGRESS)
        activeRooms[roomId] = startedRoom

        val turnDeadline = System.currentTimeMillis() + (room.config.turnTimeoutSeconds * 1000L)
        broadcastToRoom(roomId, ServerPacket.MatchStarted(roomId = roomId, firstSeatIndex = 0, turnDeadlineTimestamp = turnDeadline))
        broadcastToRoom(roomId, ServerPacket.RoomUpdated(startedRoom))

        return Result.success(startedRoom)
    }

    override suspend fun disconnect(userId: String) {
        val roomId = playerToRoomId.remove(userId) ?: return
        val room = activeRooms[roomId] ?: return
        val player = room.players.firstOrNull { it.id == userId } ?: return

        broadcastToRoom(roomId, ServerPacket.PlayerDisconnected(seatIndex = player.seatIndex, gracePeriodSeconds = 60))
    }

    private suspend fun broadcastToRoom(roomId: String, packet: ServerPacket) {
        val room = activeRooms[roomId] ?: return
        for (player in room.players) {
            getOrCreateChannel(player.id).emit(packet)
        }
    }
}
