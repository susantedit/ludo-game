package game.ludora.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey val historyId: String,
    val matchId: String,
    val gameType: String,
    val mode: String,
    val playerCount: Int,
    val result: String,
    val finalPlacement: Int,
    val turnsPlayed: Int,
    val tokensCaptured: Int,
    val xpEarned: Long,
    val coinsEarned: Long,
    val playedTimestamp: Long
)
