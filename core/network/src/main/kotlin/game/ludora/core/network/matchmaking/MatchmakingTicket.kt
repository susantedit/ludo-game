package game.ludora.core.network.matchmaking

import game.ludora.core.model.GameType
import kotlinx.serialization.Serializable

@Serializable
data class MatchmakingTicket(
    val ticketId: String,
    val playerId: String,
    val displayName: String,
    val gameType: GameType = GameType.LUDO,
    val isRanked: Boolean = false,
    val mmr: Int = 1000,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
