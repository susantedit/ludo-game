package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
data class MatchReward(
    val xpEarned: Long,
    val coinsEarned: Long,
    val isLevelUp: Boolean,
    val oldLevel: Int,
    val newLevel: Int,
    val placement: Int,
    val captures: Int,
    val unlockedCosmetic: CosmeticItem? = null
)
