package game.ludora.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import game.ludora.core.database.dao.LocalProfileDao
import game.ludora.core.database.dao.MatchHistoryDao
import game.ludora.core.database.entity.LocalProfileEntity
import game.ludora.core.database.entity.MatchHistoryEntity

@Database(
    entities = [
        LocalProfileEntity::class,
        MatchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LudoraDatabase : RoomDatabase() {
    abstract fun localProfileDao(): LocalProfileDao
    abstract fun matchHistoryDao(): MatchHistoryDao
}
