package game.ludora.core.common.ads

import game.ludora.core.model.AdPolicyConfig

/**
 * Manages frequency caps, cooldown periods, offline suppression, and entitlement states
 * for all ad placements across the application.
 */
class AdPolicyManager(
    val config: AdPolicyConfig = AdPolicyConfig(),
    initialIsAdFree: Boolean = false,
    initialSessionCount: Int = 1
) {
    var isAdFree: Boolean = initialIsAdFree
        private set

    var sessionCount: Int = initialSessionCount
        private set

    var matchesPlayedSinceLastInterstitial: Int = 0
        private set

    var lastInterstitialTimestampMs: Long = 0L
        private set

    private val interstitialTimestamps: MutableList<Long> = mutableListOf()
    private val rewardedTimestamps: MutableList<Long> = mutableListOf()

    fun setAdFree(adFree: Boolean) {
        this.isAdFree = adFree
    }

    fun recordSessionStarted() {
        sessionCount++
    }

    fun recordMatchFinished() {
        matchesPlayedSinceLastInterstitial++
    }

    fun recordInterstitialShown(currentTimeMs: Long = System.currentTimeMillis()) {
        lastInterstitialTimestampMs = currentTimeMs
        interstitialTimestamps.add(currentTimeMs)
        matchesPlayedSinceLastInterstitial = 0
    }

    fun recordRewardedShown(currentTimeMs: Long = System.currentTimeMillis()) {
        rewardedTimestamps.add(currentTimeMs)
    }

    /**
     * Banners are allowed only when the device is online and the user has not purchased Ad-Free.
     */
    fun canShowBanner(isOnline: Boolean): Boolean {
        if (!isOnline || isAdFree) return false
        return true
    }

    /**
     * Interstitials are allowed when:
     * 1. The device is online.
     * 2. The user is not Ad-Free.
     * 3. The player is past the initial onboarding session grace period.
     * 4. At least [AdPolicyConfig.minMatchesBetweenInterstitials] matches finished since last ad.
     * 5. At least [AdPolicyConfig.minMinutesBetweenInterstitials] minutes elapsed since last ad.
     * 6. Hourly cap [AdPolicyConfig.maxInterstitialsPerHour] has not been exceeded.
     */
    fun canShowInterstitial(
        isOnline: Boolean,
        currentTimeMs: Long = System.currentTimeMillis()
    ): Boolean {
        if (!isOnline || isAdFree) return false
        if (sessionCount < config.initialSessionsGracePeriod) return false
        if (matchesPlayedSinceLastInterstitial < config.minMatchesBetweenInterstitials) return false

        val minIntervalMs = config.minMinutesBetweenInterstitials * 60 * 1000L
        if (lastInterstitialTimestampMs > 0L && (currentTimeMs - lastInterstitialTimestampMs) < minIntervalMs) {
            return false
        }

        pruneOldTimestamps(interstitialTimestamps, currentTimeMs, 60 * 60 * 1000L)
        if (interstitialTimestamps.size >= config.maxInterstitialsPerHour) {
            return false
        }

        return true
    }

    /**
     * Rewarded ads require an active internet connection and respect a 24-hour frequency cap.
     * Rewarded ads remain available to Ad-Free players so they can opt in for bonus coins.
     */
    fun canShowRewarded(
        isOnline: Boolean,
        currentTimeMs: Long = System.currentTimeMillis()
    ): Boolean {
        if (!isOnline) return false
        pruneOldTimestamps(rewardedTimestamps, currentTimeMs, 24 * 60 * 60 * 1000L)
        return rewardedTimestamps.size < config.maxRewardedPer24Hours
    }

    /**
     * Remaining rewarded ads permitted within the rolling 24-hour window.
     */
    fun remainingRewardedAdsToday(currentTimeMs: Long = System.currentTimeMillis()): Int {
        pruneOldTimestamps(rewardedTimestamps, currentTimeMs, 24 * 60 * 60 * 1000L)
        return (config.maxRewardedPer24Hours - rewardedTimestamps.size).coerceAtLeast(0)
    }

    private fun pruneOldTimestamps(list: MutableList<Long>, currentTimeMs: Long, windowMs: Long) {
        val cutoff = currentTimeMs - windowMs
        list.removeAll { it < cutoff }
    }
}
