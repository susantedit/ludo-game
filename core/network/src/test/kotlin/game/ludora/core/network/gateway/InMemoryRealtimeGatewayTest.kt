package game.ludora.core.network.gateway

import game.ludora.core.network.model.ClientPacket
import game.ludora.core.network.model.RoomConfig
import game.ludora.core.network.model.RoomStatus
import game.ludora.core.network.model.ServerPacket
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryRealtimeGatewayTest {

    @Test
    fun testRoomCreationAndCodeGeneration() = runBlocking {
        val gateway = InMemoryRealtimeGateway()
        val room = gateway.createRoom(
            hostUserId = "host_1",
            hostDisplayName = "Alice",
            config = RoomConfig(maxPlayers = 2)
        )

        assertNotNull(room.roomId)
        assertEquals(6, room.roomCode.length)
        assertEquals(RoomStatus.LOBBY, room.status)
        assertEquals(1, room.players.size)
        assertTrue(room.players[0].isHost)
        assertTrue(room.players[0].isReady)
    }

    @Test
    fun testPlayerJoinAndReadyWorkflow() = runBlocking {
        val gateway = InMemoryRealtimeGateway()
        val hostRoom = gateway.createRoom("user_alice", "Alice", RoomConfig(maxPlayers = 2))

        val joinResult = gateway.joinRoom("user_bob", "Bob", hostRoom.roomCode)
        assertTrue(joinResult.isSuccess)
        val roomAfterJoin = joinResult.getOrThrow()

        assertEquals(2, roomAfterJoin.players.size)
        val bob = roomAfterJoin.players.first { it.id == "user_bob" }
        assertEquals(1, bob.seatIndex)
        assertFalse(bob.isReady)

        // Cannot start before Bob is ready
        val prematureStart = gateway.startMatch("user_alice", hostRoom.roomId)
        assertFalse("Cannot start match while player is not ready", prematureStart.isSuccess)

        // Bob toggles ready
        val readyResult = gateway.toggleReady("user_bob", hostRoom.roomId, isReady = true)
        assertTrue(readyResult.isSuccess)
        assertTrue(readyResult.getOrThrow().canStart)

        // Alice starts match
        val startResult = gateway.startMatch("user_alice", hostRoom.roomId)
        assertTrue(startResult.isSuccess)
        assertEquals(RoomStatus.IN_PROGRESS, startResult.getOrThrow().status)
    }

    @Test
    fun testPingPongHeartbeat() = runBlocking {
        val gateway = InMemoryRealtimeGateway()
        val eventsFlow = gateway.observeEvents("user_test")

        val sendTimestamp = 123456789L
        gateway.sendPacket("user_test", ClientPacket.Ping(timestamp = sendTimestamp))

        val response = eventsFlow.first()
        assertTrue(response is ServerPacket.Pong)
        assertEquals(sendTimestamp, (response as ServerPacket.Pong).originalTimestamp)
    }

    @Test
    fun testFullRoomRejectsAdditionalPlayers() = runBlocking {
        val gateway = InMemoryRealtimeGateway()
        val room = gateway.createRoom("host_p1", "P1", RoomConfig(maxPlayers = 2))

        val p2Join = gateway.joinRoom("guest_p2", "P2", room.roomCode)
        assertTrue(p2Join.isSuccess)

        val p3Join = gateway.joinRoom("guest_p3", "P3", room.roomCode)
        assertFalse("Third player cannot join a 2-player max room", p3Join.isSuccess)
    }
}
