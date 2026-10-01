package game.ludora.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import game.ludora.core.database.entity.LocalProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalProfileDao {

    @Query("SELECT * FROM local_profiles LIMIT 1")
    fun getActiveProfile(): Flow<LocalProfileEntity?>

    @Query("SELECT * FROM local_profiles WHERE profileId = :profileId LIMIT 1")
    suspend fun getProfileById(profileId: String): LocalProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: LocalProfileEntity)

    @Update
    suspend fun updateProfile(profile: LocalProfileEntity)

    @Query("UPDATE local_profiles SET coins = coins + :amount WHERE profileId = :profileId")
    suspend fun addCoins(profileId: String, amount: Long)

    @Query("UPDATE local_profiles SET experiencePoints = experiencePoints + :amount WHERE profileId = :profileId")
    suspend fun addExperience(profileId: String, amount: Long)
}
