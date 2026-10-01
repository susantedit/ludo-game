package game.ludora.core.common.ads

import game.ludora.core.model.AdPlacement
import game.ludora.core.model.AdReward
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdIntegrationTest {

    @Test
    fun `fake ad provider handles banner lifecycle cleanly`() {
        val provider = FakeAdProvider()
        assertFalse(provider.isInitialized)

        provider.initialize()
        assertTrue(provider.isInitialized)

        var bannerLoaded = false
        provider.showBanner(
            onLoaded = { bannerLoaded = true },
            onFailed = { }
        )
        assertTrue(bannerLoaded)
        assertTrue(provider.bannerVisible)

        provider.hideBanner()
        assertFalse(provider.bannerVisible)
    }

    @Test
    fun `fake ad provider delivers rewarded video callback`() {
        val provider = FakeAdProvider()
        provider.initialize()

        var earnedReward: AdReward? = null
        var closed = false

        provider.showRewarded(
            placement = AdPlacement.REWARDED_DAILY_BONUS,
            onRewarded = { reward -> earnedReward = reward },
            onClosed = { closed = true },
            onFailed = { }
        )

        assertTrue(closed)
        assertEquals(1, provider.rewardedImpressions)
        assertTrue(earnedReward is AdReward.Coins)
        assertEquals(50, (earnedReward as AdReward.Coins).amount)
    }

    @Test
    fun `fake billing service grants and restores ad free pass`() = runBlocking {
        val billing = FakeBillingService(initialAdFree = false)
        assertFalse(billing.isAdFree())

        val purchaseResult = billing.purchaseRemoveAds()
        assertTrue(purchaseResult.isSuccess)
        assertTrue(billing.isAdFree())
        assertEquals(1, billing.purchaseAttempts)

        val restoreResult = billing.restorePurchases()
        assertTrue(restoreResult.isSuccess)
        assertEquals(true, restoreResult.getOrNull())
        assertEquals(1, billing.restoreAttempts)
    }

    @Test
    fun `server side reward verifier verifies valid signatures and rejects tampered ones`() {
        val verifier = ServerSideRewardVerifier(verificationSecret = "test_secret_key_123")
        val timestamp = 1_700_000_000_000L

        val validSig = verifier.generateSignature(
            userId = "user_42",
            placement = "REWARDED_DAILY_BONUS",
            rewardAmount = 50,
            timestampMs = timestamp,
            nonce = "random_nonce_99"
        )

        // Valid signature matches
        val isValid = verifier.verifyReward(
            userId = "user_42",
            placement = "REWARDED_DAILY_BONUS",
            rewardAmount = 50,
            timestampMs = timestamp,
            nonce = "random_nonce_99",
            signature = validSig,
            currentTimeMs = timestamp + 10_000L
        )
        assertTrue(isValid)

        // Tampered amount rejected
        val isTamperedAmount = verifier.verifyReward(
            userId = "user_42",
            placement = "REWARDED_DAILY_BONUS",
            rewardAmount = 500, // Attacker inflated amount
            timestampMs = timestamp,
            nonce = "random_nonce_99",
            signature = validSig,
            currentTimeMs = timestamp + 10_000L
        )
        assertFalse(isTamperedAmount)

        // Expired timestamp (>5 min drift) rejected
        val isExpired = verifier.verifyReward(
            userId = "user_42",
            placement = "REWARDED_DAILY_BONUS",
            rewardAmount = 50,
            timestampMs = timestamp,
            nonce = "random_nonce_99",
            signature = validSig,
            currentTimeMs = timestamp + 10 * 60 * 1000L // 10 minutes later
        )
        assertFalse(isExpired)
    }
}
