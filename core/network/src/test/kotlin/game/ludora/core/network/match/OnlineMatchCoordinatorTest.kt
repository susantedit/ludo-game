package game.ludora.core.network.match

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnlineMatchCoordinatorTest {

    @Test
    fun testValidRollIncrementsSequenceId() {
        val coordinator = OnlineMatchCoordinator("room_123", turnDurationSeconds = 15)
        assertEquals(1L, coordinator.getSequenceId())

        val rollResult = coordinator.processRollIntent(seatIndex = 0)
        assertTrue(rollResult.isSuccess)
        assertEquals(2L, coordinator.getSequenceId())
    }

    @Test
    fun testOutOfTurnActionRejected() {
        val coordinator = OnlineMatchCoordinator("room_123", turnDurationSeconds = 15)
        // Seat 1 tries to roll while seat 0 is active
        val invalidRoll = coordinator.processRollIntent(seatIndex = 1)
        assertFalse(invalidRoll.isSuccess)
        assertEquals(1L, coordinator.getSequenceId())
    }

    @Test
    fun testTurnTimeoutAdvancesTurn() {
        val coordinator = OnlineMatchCoordinator("room_123", turnDurationSeconds = 15)
        val initialSeat = coordinator.getActiveSeatIndex()

        val nextState = coordinator.handleTurnTimeout()
        assertEquals(2L, coordinator.getSequenceId())
        assertEquals((initialSeat + 1) % 2, nextState.activeSeatIndex)
    }

    @Test
    fun testReconnectionWithinGracePeriod() {
        val coordinator = OnlineMatchCoordinator("room_123", turnDurationSeconds = 15)
        coordinator.onPlayerDisconnected(seatIndex = 0)

        // Reconnect 10 seconds later
        val reconnectResult = coordinator.onPlayerReconnected(seatIndex = 0)
        assertTrue(reconnectResult.isSuccess)
        val snapshot = reconnectResult.getOrThrow()
        assertEquals("room_123", snapshot.roomId)
        assertEquals(1L, snapshot.sequenceId)
    }

    @Test
    fun testReconnectionExpiredAfter60Seconds() {
        val coordinator = OnlineMatchCoordinator("room_123", turnDurationSeconds = 15)
        coordinator.onPlayerDisconnected(seatIndex = 0)

        val expiredTime = System.currentTimeMillis() + 65_000L
        val expiredResult = coordinator.onPlayerReconnected(seatIndex = 0, currentTimeMs = expiredTime)
        assertFalse("Reconnection must fail after 60s grace period", expiredResult.isSuccess)
    }
}
