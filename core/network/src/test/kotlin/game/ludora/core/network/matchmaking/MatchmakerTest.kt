package game.ludora.core.network.matchmaking

import game.ludora.core.model.GameType
import game.ludora.core.network.gateway.InMemoryRealtimeGateway
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchmakerTest {

    @Test
    fun testEnqueueAndDequeue() {
        val matchmaker = Matchmaker()
        val ticket = MatchmakingTicket(
            ticketId = "t1",
            playerId = "p1",
            displayName = "Player 1",
            gameType = GameType.LUDO
        )

        matchmaker.enqueue(ticket)
        assertEquals(1, matchmaker.getQueueSize())

        val removed = matchmaker.dequeue("t1")
        assertTrue(removed)
        assertEquals(0, matchmaker.getQueueSize())
    }

    @Test
    fun testPairingCompatibleTickets() = runBlocking {
        val matchmaker = Matchmaker()
        val gateway = InMemoryRealtimeGateway()

        val t1 = MatchmakingTicket("t1", "p1", "Player 1", GameType.LUDO, mmr = 1000)
        val t2 = MatchmakingTicket("t2", "p2", "Player 2", GameType.LUDO, mmr = 1050)

        matchmaker.enqueue(t1)
        matchmaker.enqueue(t2)

        val rooms = matchmaker.processQueue(gateway, requiredPlayers = 2)
        assertEquals(1, rooms.size)
        assertEquals(0, matchmaker.getQueueSize())
        assertEquals(2, rooms[0].config.maxPlayers)
    }

    @Test
    fun testDifferentGameTypesAreNotMatched() = runBlocking {
        val matchmaker = Matchmaker()
        val gateway = InMemoryRealtimeGateway()

        val ludoTicket = MatchmakingTicket("t1", "p1", "Player 1", GameType.LUDO, mmr = 1000)
        val snakeTicket = MatchmakingTicket("t2", "p2", "Player 2", GameType.SNAKE_AND_LADDER, mmr = 1000)

        matchmaker.enqueue(ludoTicket)
        matchmaker.enqueue(snakeTicket)

        val rooms = matchmaker.processQueue(gateway, requiredPlayers = 2)
        assertTrue(rooms.isEmpty())
        assertEquals(2, matchmaker.getQueueSize())
    }

    @Test
    fun testTimeoutDetectionAndAiAutofill() = runBlocking {
        val matchmaker = Matchmaker()
        val gateway = InMemoryRealtimeGateway()

        val oldTimestamp = System.currentTimeMillis() - 50_000L // 50s ago
        val ticket = MatchmakingTicket("t1", "p1", "Lonely Player", GameType.LUDO, createdAtTimestamp = oldTimestamp)

        matchmaker.enqueue(ticket)
        val timedOut = matchmaker.getTimedOutTickets()
        assertEquals(1, timedOut.size)

        val room = matchmaker.autofillWithAi(ticket, gateway, totalPlayers = 2)
        assertEquals(2, room.config.maxPlayers)
        assertEquals(0, matchmaker.getQueueSize())
    }
}
