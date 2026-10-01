package game.ludora.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import game.ludora.core.database.entity.MatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchHistoryDao {

    @Query("SELECT * FROM match_history ORDER BY playedTimestamp DESC LIMIT :limit")
    fun getRecentMatches(limit: Int = 50): Flow<List<MatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchRecord(match: MatchHistoryEntity)

    @Query("SELECT COUNT(*) FROM match_history")
    suspend fun getTotalMatchCount(): Int

    @Query("DELETE FROM match_history WHERE historyId NOT IN (SELECT historyId FROM match_history ORDER BY playedTimestamp DESC LIMIT 50)")
    suspend fun pruneOldMatches()
}
