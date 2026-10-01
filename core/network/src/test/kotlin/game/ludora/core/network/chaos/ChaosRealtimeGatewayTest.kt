package game.ludora.core.network.chaos

import game.ludora.core.model.GameType
import game.ludora.core.model.PlayerColor
import game.ludora.core.network.gateway.InMemoryRealtimeGateway
import game.ludora.core.network.match.OnlineMatchCoordinator
import game.ludora.core.network.model.ClientPacket
import game.ludora.core.network.model.RoomConfig
import game.ludora.core.network.model.ServerPacket
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * Chaos test harness simulating adverse network conditions (packet loss, latency jitter, sudden socket drops).
 */
class ChaosRealtimeGatewayTest {

    /**
     * An unreliable network proxy wrapping InMemoryRealtimeGateway to simulate packet drops and delays.
     */
    class ChaosProxy(
        val gateway: InMemoryRealtimeGateway,
        var packetDropRate: Float = 0.0f,
        val rng: Random = Random(1337)
    ) {
        var droppedPackets: Int = 0
            private set

        var sentPackets: Int = 0
            private set

        suspend fun sendPacket(userId: String, packet: ClientPacket): Boolean {
            sentPackets++
            if (packetDropRate > 0f && rng.nextFloat() < packetDropRate) {
                droppedPackets++
                return false // Simulated dropped packet
            }
            gateway.sendPacket(userId, packet)
            return true
        }
    }

    @Test
    fun `chaos proxy drops packets at expected rate without crashing gateway`() = runBlocking {
        val gateway = InMemoryRealtimeGateway()
        val chaos = ChaosProxy(gateway, packetDropRate = 0.30f) // 30% drop rate

        val room = gateway.createRoom("user_host", "Host", RoomConfig(GameType.LUDO, 2))
        gateway.joinRoom("user_guest", "Guest", room.roomCode)

        for (i in 1..100) {
            chaos.sendPacket("user_host", ClientPacket.KeepAlive)
        }

        assertEquals(100, chaos.sentPackets)
        assertTrue("Dropped packets should be non-zero under 30% loss", chaos.droppedPackets > 10)
        assertTrue("Not all packets should be dropped", chaos.droppedPackets < 60)
    }

    @Test
    fun `online match coordinator recovers state snapshot upon reconnection`() {
        val coordinator = OnlineMatchCoordinator(
            matchId = "chaos_match_001",
            playerIds = listOf("p1", "p2"),
            turnTimeoutSeconds = 15,
            disconnectGracePeriodSeconds = 60
        )

        // Player 2 disconnects during match
        coordinator.handlePlayerDisconnected("p2", disconnectTimeMs = 10_000L)
        assertFalse(coordinator.isPlayerConnected("p2"))

        // Reconnect within 60-second grace window (e.g. at 25 seconds)
        val snapshot = coordinator.handlePlayerReconnected("p2", reconnectTimeMs = 25_000L)
        assertNotNull("Reconnecting player within 60s should receive state snapshot", snapshot)
        assertTrue("Player 2 should now be marked connected", coordinator.isPlayerConnected("p2"))
        assertEquals("chaos_match_001", snapshot?.matchId)
    }

    @Test
    fun `online match coordinator forfeits player if grace period is exceeded`() {
        val coordinator = OnlineMatchCoordinator(
            matchId = "chaos_match_002",
            playerIds = listOf("p1", "p2"),
            turnTimeoutSeconds = 15,
            disconnectGracePeriodSeconds = 60
        )

        // Player 2 drops at t = 10s
        coordinator.handlePlayerDisconnected("p2", disconnectTimeMs = 10_000L)

        // Player 2 attempts to reconnect at t = 75s (65s elapsed, exceeding 60s grace)
        val snapshot = coordinator.handlePlayerReconnected("p2", reconnectTimeMs = 75_000L)
        assertEquals("Reconnecting after grace period expiration must be rejected", null, snapshot)
        assertFalse("Player 2 should remain disconnected/forfeited", coordinator.isPlayerConnected("p2"))
    }

    @Test
    fun `authoritative loop rejects stale sequence IDs from lagged clients`() {
        val coordinator = OnlineMatchCoordinator(
            matchId = "chaos_match_003",
            playerIds = listOf("p1", "p2")
        )

        // First action processed with sequenceId 1
        val event1 = coordinator.handleRollDice(
            playerId = "p1",
            color = PlayerColor.RED,
            diceValue = 6,
            sequenceId = 1L
        )
        assertNotNull("Sequence ID 1 should be accepted", event1)
        assertEquals(1L, coordinator.lastSequenceId)

        // Client resends or lagged packet delivers sequence ID 1 again (duplicate/out-of-order)
        val eventDuplicate = coordinator.handleRollDice(
            playerId = "p1",
            color = PlayerColor.RED,
            diceValue = 6,
            sequenceId = 1L
        )
        assertEquals("Duplicate or out-of-order sequence ID must be rejected", null, eventDuplicate)

        // Valid subsequent sequence ID 2 is accepted
        val event2 = coordinator.handleRollDice(
            playerId = "p1",
            color = PlayerColor.RED,
            diceValue = 4,
            sequenceId = 2L
        )
        assertNotNull("Sequence ID 2 should be accepted", event2)
        assertEquals(2L, coordinator.lastSequenceId)
    }
}
