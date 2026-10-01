package game.ludora.core.common.ads

import game.ludora.core.model.AdPolicyConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdPolicyManagerTest {

    @Test
    fun `banner ad is suppressed when offline`() {
        val manager = AdPolicyManager()
        assertFalse(manager.canShowBanner(isOnline = false))
        assertTrue(manager.canShowBanner(isOnline = true))
    }

    @Test
    fun `banner ad is suppressed when user is ad-free`() {
        val manager = AdPolicyManager(initialIsAdFree = true)
        assertFalse(manager.canShowBanner(isOnline = true))

        manager.setAdFree(false)
        assertTrue(manager.canShowBanner(isOnline = true))
    }

    @Test
    fun `interstitial is suppressed during initial onboarding sessions`() {
        val manager = AdPolicyManager(initialSessionCount = 1)
        manager.recordMatchFinished()
        manager.recordMatchFinished()

        // Session 1: Suppressed
        assertFalse(manager.canShowInterstitial(isOnline = true))

        // Session 2: Suppressed
        manager.recordSessionStarted()
        assertFalse(manager.canShowInterstitial(isOnline = true))

        // Session 3: Allowed (meets grace period threshold of 3)
        manager.recordSessionStarted()
        assertTrue(manager.canShowInterstitial(isOnline = true))
    }

    @Test
    fun `interstitial requires minimum matches between shows`() {
        val manager = AdPolicyManager(initialSessionCount = 3)
        assertFalse(manager.canShowInterstitial(isOnline = true))

        manager.recordMatchFinished()
        assertFalse(manager.canShowInterstitial(isOnline = true))

        manager.recordMatchFinished() // 2 matches
        assertTrue(manager.canShowInterstitial(isOnline = true))

        // Show interstitial
        manager.recordInterstitialShown(currentTimeMs = 1_000_000L)
        assertEquals(0, manager.matchesPlayedSinceLastInterstitial)

        // Immediately after: suppressed due to 0 matches
        assertFalse(manager.canShowInterstitial(isOnline = true, currentTimeMs = 1_000_000L + 9 * 60 * 1000L))
    }

    @Test
    fun `interstitial enforces minimum cooldown interval`() {
        val manager = AdPolicyManager(
            config = AdPolicyConfig(minMinutesBetweenInterstitials = 8L, minMatchesBetweenInterstitials = 1),
            initialSessionCount = 3
        )

        manager.recordMatchFinished()
        assertTrue(manager.canShowInterstitial(isOnline = true, currentTimeMs = 1_000_000L))

        manager.recordInterstitialShown(currentTimeMs = 1_000_000L)
        manager.recordMatchFinished()

        // 7 minutes later: suppressed (less than 8 min)
        val sevenMinutesLater = 1_000_000L + 7 * 60 * 1000L
        assertFalse(manager.canShowInterstitial(isOnline = true, currentTimeMs = sevenMinutesLater))

        // 8 minutes 1 second later: allowed
        val eightMinutesLater = 1_000_000L + 8 * 60 * 1000L + 1000L
        assertTrue(manager.canShowInterstitial(isOnline = true, currentTimeMs = eightMinutesLater))
    }

    @Test
    fun `interstitial enforces hourly cap`() {
        val manager = AdPolicyManager(
            config = AdPolicyConfig(
                minMinutesBetweenInterstitials = 1L,
                minMatchesBetweenInterstitials = 1,
                maxInterstitialsPerHour = 3
            ),
            initialSessionCount = 3
        )

        val baseTime = 10_000_000L

        // Ad 1
        manager.recordMatchFinished()
        assertTrue(manager.canShowInterstitial(isOnline = true, currentTimeMs = baseTime))
        manager.recordInterstitialShown(currentTimeMs = baseTime)

        // Ad 2
        manager.recordMatchFinished()
        val time2 = baseTime + 2 * 60 * 1000L
        assertTrue(manager.canShowInterstitial(isOnline = true, currentTimeMs = time2))
        manager.recordInterstitialShown(currentTimeMs = time2)

        // Ad 3
        manager.recordMatchFinished()
        val time3 = baseTime + 4 * 60 * 1000L
        assertTrue(manager.canShowInterstitial(isOnline = true, currentTimeMs = time3))
        manager.recordInterstitialShown(currentTimeMs = time3)

        // Ad 4 within same hour: suppressed by hourly cap
        manager.recordMatchFinished()
        val time4 = baseTime + 6 * 60 * 1000L
        assertFalse(manager.canShowInterstitial(isOnline = true, currentTimeMs = time4))

        // After 61 minutes: past the 1-hour window for Ad 1, so allowed again
        val timeAfterHour = baseTime + 61 * 60 * 1000L
        assertTrue(manager.canShowInterstitial(isOnline = true, currentTimeMs = timeAfterHour))
    }

    @Test
    fun `rewarded ads enforce 24-hour cap and track remaining quota`() {
        val manager = AdPolicyManager(
            config = AdPolicyConfig(maxRewardedPer24Hours = 5)
        )

        val baseTime = 100_000_000L

        assertEquals(5, manager.remainingRewardedAdsToday(baseTime))
        assertTrue(manager.canShowRewarded(isOnline = true, currentTimeMs = baseTime))

        // Suppressed when offline
        assertFalse(manager.canShowRewarded(isOnline = false, currentTimeMs = baseTime))

        // Watch 5 ads
        for (i in 1..5) {
            manager.recordRewardedShown(currentTimeMs = baseTime + i * 1000L)
        }

        assertEquals(0, manager.remainingRewardedAdsToday(baseTime + 10_000L))
        assertFalse(manager.canShowRewarded(isOnline = true, currentTimeMs = baseTime + 10_000L))

        // 25 hours later: quota resets
        val nextDay = baseTime + 25 * 60 * 60 * 1000L
        assertEquals(5, manager.remainingRewardedAdsToday(nextDay))
        assertTrue(manager.canShowRewarded(isOnline = true, currentTimeMs = nextDay))
    }

    @Test
    fun `rewarded ads are available even if user is ad-free`() {
        val manager = AdPolicyManager(initialIsAdFree = true)
        assertTrue(manager.canShowRewarded(isOnline = true))
        assertFalse(manager.canShowBanner(isOnline = true))
        assertFalse(manager.canShowInterstitial(isOnline = true))
    }
}
