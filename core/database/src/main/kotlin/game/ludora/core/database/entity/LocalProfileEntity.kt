package game.ludora.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import game.ludora.core.model.LocalProfile

@Entity(tableName = "local_profiles")
data class LocalProfileEntity(
    @PrimaryKey val profileId: String,
    val displayName: String,
    val avatarId: String,
    val frameId: String,
    val experiencePoints: Long,
    val level: Int,
    val coins: Long,
    val statsMatchesPlayed: Int,
    val statsMatchesWon: Int,
    val statsTokensCaptured: Int,
    val lastActiveTimestamp: Long
) {
    fun toDomainModel(): LocalProfile = LocalProfile(
        profileId = profileId,
        displayName = displayName,
        avatarId = avatarId,
        frameId = frameId,
        experiencePoints = experiencePoints,
        level = level,
        coins = coins,
        statsMatchesPlayed = statsMatchesPlayed,
        statsMatchesWon = statsMatchesWon,
        statsTokensCaptured = statsTokensCaptured,
        lastActiveTimestamp = lastActiveTimestamp
    )

    companion object {
        fun fromDomainModel(model: LocalProfile): LocalProfileEntity = LocalProfileEntity(
            profileId = model.profileId,
            displayName = model.displayName,
            avatarId = model.avatarId,
            frameId = model.frameId,
            experiencePoints = model.experiencePoints,
            level = model.level,
            coins = model.coins,
            statsMatchesPlayed = model.statsMatchesPlayed,
            statsMatchesWon = model.statsMatchesWon,
            statsTokensCaptured = model.statsTokensCaptured,
            lastActiveTimestamp = model.lastActiveTimestamp
        )
    }
}
