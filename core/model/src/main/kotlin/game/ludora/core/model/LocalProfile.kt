package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalProfile(
    val profileId: String,
    val displayName: String = "Player",
    val avatarId: String = "avatar_classic",
    val frameId: String = "frame_default",
    val experiencePoints: Long = 0L,
    val level: Int = 1,
    val coins: Long = 100L,
    val statsMatchesPlayed: Int = 0,
    val statsMatchesWon: Int = 0,
    val statsTokensCaptured: Int = 0,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
) {
    val winRate: Float
        get() = if (statsMatchesPlayed > 0) {
            (statsMatchesWon.toFloat() / statsMatchesPlayed.toFloat()) * 100f
        } else {
            0f
        }
}
