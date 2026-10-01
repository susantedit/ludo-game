package game.ludora.core.model

import kotlinx.serialization.Serializable

/**
 * Ad placement boundaries in the application.
 */
@Serializable
enum class AdPlacement {
    BANNER_HOME,
    INTERSTITIAL_POST_MATCH,
    REWARDED_DAILY_BONUS,
    REWARDED_QUEST_REROLL,
    REWARDED_COSMETIC_SPIN
}

/**
 * Rewards delivered upon successful completion of a rewarded video ad.
 */
@Serializable
sealed class AdReward {
    @Serializable
    data class Coins(val amount: Int) : AdReward()

    @Serializable
    data object QuestReroll : AdReward()

    @Serializable
    data object CosmeticSpin : AdReward()
}

/**
 * Lifecycle state of an ad unit.
 */
enum class AdState {
    NOT_LOADED,
    LOADING,
    READY,
    SHOWING,
    FAILED
}

/**
 * Configuration parameters for ad display frequency and protection rules.
 */
@Serializable
data class AdPolicyConfig(
    val minMinutesBetweenInterstitials: Long = 8L,
    val minMatchesBetweenInterstitials: Int = 2,
    val initialSessionsGracePeriod: Int = 3,
    val maxInterstitialsPerHour: Int = 3,
    val maxRewardedPer24Hours: Int = 5
)
