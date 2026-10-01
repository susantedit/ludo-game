package game.ludora.core.common.ads

import game.ludora.core.model.AdPlacement
import game.ludora.core.model.AdReward
import game.ludora.core.model.AdState

/**
 * In-memory test double of [AdProvider] for offline testing, CI environments, and developer previews.
 */
class FakeAdProvider(
    var isInitialized: Boolean = false,
    var shouldFailToLoad: Boolean = false,
    var simulatedReward: AdReward = AdReward.Coins(50)
) : AdProvider {

    private val adStates = mutableMapOf<AdPlacement, AdState>()
    var bannerVisible: Boolean = false
        private set

    var interstitialImpressions: Int = 0
        private set

    var rewardedImpressions: Int = 0
        private set

    override fun initialize() {
        isInitialized = true
        for (placement in AdPlacement.entries) {
            adStates[placement] = AdState.READY
        }
    }

    override fun getAdState(placement: AdPlacement): AdState {
        return adStates[placement] ?: AdState.NOT_LOADED
    }

    override fun preloadAd(placement: AdPlacement) {
        if (shouldFailToLoad) {
            adStates[placement] = AdState.FAILED
        } else {
            adStates[placement] = AdState.READY
        }
    }

    override fun showBanner(onLoaded: () -> Unit, onFailed: (String) -> Unit) {
        if (!isInitialized || shouldFailToLoad) {
            bannerVisible = false
            onFailed("Ad failed to load or provider uninitialized")
        } else {
            bannerVisible = true
            adStates[AdPlacement.BANNER_HOME] = AdState.SHOWING
            onLoaded()
        }
    }

    override fun hideBanner() {
        bannerVisible = false
        adStates[AdPlacement.BANNER_HOME] = AdState.READY
    }

    override fun showInterstitial(onDismissed: () -> Unit, onFailed: (String) -> Unit) {
        if (!isInitialized || shouldFailToLoad) {
            onFailed("Interstitial not ready")
        } else {
            interstitialImpressions++
            onDismissed()
        }
    }

    override fun showRewarded(
        placement: AdPlacement,
        onRewarded: (AdReward) -> Unit,
        onClosed: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        if (!isInitialized || shouldFailToLoad) {
            onFailed("Rewarded video unavailable")
        } else {
            rewardedImpressions++
            val reward = when (placement) {
                AdPlacement.REWARDED_QUEST_REROLL -> AdReward.QuestReroll
                AdPlacement.REWARDED_COSMETIC_SPIN -> AdReward.CosmeticSpin
                else -> simulatedReward
            }
            onRewarded(reward)
            onClosed()
        }
    }
}
