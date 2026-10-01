package game.ludora.ads

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import game.ludora.BuildConfig
import game.ludora.core.common.ads.AdProvider
import game.ludora.core.model.AdPlacement
import game.ludora.core.model.AdReward
import game.ludora.core.model.AdState

/**
 * Production [AdProvider] powered by Google Mobile Ads SDK (AdMob).
 *
 * Automatically resolves unit IDs from [BuildConfig]:
 * - Uses official Google test IDs in debug builds.
 * - Uses real user-configured AdMob IDs in release builds.
 */
class AdMobProvider(
    private val activity: Activity,
    private val bannerAdUnitId: String = BuildConfig.BANNER_AD_ID,
    private val interstitialAdUnitId: String = BuildConfig.INTERSTITIAL_AD_ID,
    private val rewardedAdUnitId: String = BuildConfig.REWARDED_AD_ID
) : AdProvider {

    private val TAG = "AdMobProvider"

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    override fun initialize() {
        Log.d(TAG, "AdMobProvider initialized with App ID: ${BuildConfig.ADMOB_APP_ID}")
        preloadAd(AdPlacement.INTERSTITIAL_POST_MATCH)
        preloadAd(AdPlacement.REWARDED_DAILY_BONUS)
    }

    override fun getAdState(placement: AdPlacement): AdState {
        return when (placement) {
            AdPlacement.INTERSTITIAL_POST_MATCH -> {
                when {
                    interstitialAd != null -> AdState.READY
                    isInterstitialLoading -> AdState.LOADING
                    else -> AdState.NOT_LOADED
                }
            }
            AdPlacement.REWARDED_DAILY_BONUS,
            AdPlacement.REWARDED_QUEST_REROLL,
            AdPlacement.REWARDED_COSMETIC_SPIN -> {
                when {
                    rewardedAd != null -> AdState.READY
                    isRewardedLoading -> AdState.LOADING
                    else -> AdState.NOT_LOADED
                }
            }
            AdPlacement.BANNER_HOME -> AdState.READY
        }
    }

    override fun preloadAd(placement: AdPlacement) {
        when (placement) {
            AdPlacement.INTERSTITIAL_POST_MATCH -> preloadInterstitial()
            AdPlacement.REWARDED_DAILY_BONUS,
            AdPlacement.REWARDED_QUEST_REROLL,
            AdPlacement.REWARDED_COSMETIC_SPIN -> preloadRewarded()
            AdPlacement.BANNER_HOME -> Unit
        }
    }

    private fun preloadInterstitial() {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            interstitialAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad successfully loaded.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    private fun preloadRewarded() {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            activity,
            rewardedAdUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Rewarded ad successfully loaded.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.w(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    override fun showBanner(onLoaded: () -> Unit, onFailed: (String) -> Unit) {
        onLoaded()
    }

    override fun hideBanner() {
        // Managed by Compose AdView lifecycle
    }

    override fun showInterstitial(onDismissed: () -> Unit, onFailed: (String) -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadInterstitial()
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    preloadInterstitial()
                    onFailed(adError.message)
                }
            }
            ad.show(activity)
        } else {
            preloadInterstitial()
            onFailed("Interstitial ad not ready.")
        }
    }

    override fun showRewarded(
        placement: AdPlacement,
        onRewarded: (AdReward) -> Unit,
        onClosed: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        val ad = rewardedAd
        if (ad != null) {
            var rewardGranted = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewarded()
                    onClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    preloadRewarded()
                    onFailed(adError.message)
                }
            }

            ad.show(activity) { rewardItem ->
                rewardGranted = true
                val reward = when (placement) {
                    AdPlacement.REWARDED_QUEST_REROLL -> AdReward.QuestReroll
                    AdPlacement.REWARDED_COSMETIC_SPIN -> AdReward.CosmeticSpin
                    else -> AdReward.Coins(if (rewardItem.amount > 0) rewardItem.amount else 50)
                }
                onRewarded(reward)
            }
        } else {
            preloadRewarded()
            onFailed("Rewarded video is still loading. Please try again in a few seconds.")
        }
    }
}
