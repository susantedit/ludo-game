package game.ludora.core.network.matchmaking

import game.ludora.core.model.GameType
import game.ludora.core.network.gateway.RealtimeGateway
import game.ludora.core.network.model.RoomConfig
import game.ludora.core.network.model.RoomDetails
import java.util.UUID
import kotlin.math.abs

/**
 * Server-authoritative matchmaking engine pairing players by game type, MMR proximity,
 * and managing the 45-second timeout with automated AI autofill fallback.
 */
class Matchmaker {

    companion object {
        const val DEFAULT_TIMEOUT_MS = 45_000L
        const val MAX_MMR_DELTA = 300
    }

    private val queue = mutableListOf<MatchmakingTicket>()

    fun enqueue(ticket: MatchmakingTicket) {
        if (queue.none { it.ticketId == ticket.ticketId || it.playerId == ticket.playerId }) {
            queue.add(ticket)
        }
    }

    fun dequeue(ticketId: String): Boolean {
        return queue.removeIf { it.ticketId == ticketId }
    }

    fun getQueueSize(): Int = queue.size

    /**
     * Scans the ticket pool for compatible players and allocates authoritative rooms.
     */
    suspend fun processQueue(
        gateway: RealtimeGateway,
        requiredPlayers: Int = 2
    ): List<RoomDetails> {
        val createdRooms = mutableListOf<RoomDetails>()
        val groupedByType = queue.groupBy { it.gameType to it.isRanked }

        for ((_, tickets) in groupedByType) {
            val sorted = tickets.sortedBy { it.mmr }
            var i = 0
            while (i + requiredPlayers <= sorted.size) {
                val candidateBatch = sorted.subList(i, i + requiredPlayers)
                val mmrSpread = candidateBatch.maxOf { it.mmr } - candidateBatch.minOf { it.mmr }

                if (mmrSpread <= MAX_MMR_DELTA) {
                    val host = candidateBatch.first()
                    val room = gateway.createRoom(
                        hostUserId = host.playerId,
                        hostDisplayName = host.displayName,
                        config = RoomConfig(
                            gameType = host.gameType,
                            maxPlayers = requiredPlayers,
                            isPrivate = false
                        )
                    )

                    for (j in 1 until candidateBatch.size) {
                        val guest = candidateBatch[j]
                        gateway.joinRoom(guest.playerId, guest.displayName, room.roomCode)
                        gateway.toggleReady(guest.playerId, room.roomId, isReady = true)
                    }

                    // Remove matched tickets
                    candidateBatch.forEach { queue.remove(it) }
                    createdRooms.add(room)
                    i += requiredPlayers
                } else {
                    i++
                }
            }
        }

        return createdRooms
    }

    /**
     * Checks for tickets that have exceeded the search timeout.
     */
    fun getTimedOutTickets(currentTimeMs: Long = System.currentTimeMillis()): List<MatchmakingTicket> {
        return queue.filter { (currentTimeMs - it.createdAtTimestamp) >= DEFAULT_TIMEOUT_MS }
    }

    /**
     * Autofills remaining player slots with AI opponents when timeout occurs.
     */
    suspend fun autofillWithAi(
        ticket: MatchmakingTicket,
        gateway: RealtimeGateway,
        totalPlayers: Int = 2
    ): RoomDetails {
        queue.remove(ticket)

        val room = gateway.createRoom(
            hostUserId = ticket.playerId,
            hostDisplayName = ticket.displayName,
            config = RoomConfig(
                gameType = ticket.gameType,
                maxPlayers = totalPlayers,
                isPrivate = false
            )
        )

        for (i in 1 until totalPlayers) {
            val aiId = "ai_${UUID.randomUUID().toString().take(6)}"
            val aiName = "Bot ${i}"
            gateway.joinRoom(aiId, aiName, room.roomCode)
            gateway.toggleReady(aiId, room.roomId, isReady = true)
        }

        return room
    }
}
