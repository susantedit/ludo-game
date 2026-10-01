package game.ludora.core.common.ads

import game.ludora.core.model.AdPlacement
import game.ludora.core.model.AdReward
import game.ludora.core.model.AdState

/**
 * Abstraction layer decoupling the game client from third-party advertising SDKs.
 */
interface AdProvider {
    /**
     * Initializes the advertising network SDK.
     */
    fun initialize()

    /**
     * Returns the current state of an ad placement.
     */
    fun getAdState(placement: AdPlacement): AdState

    /**
     * Requests preloading of an ad unit.
     */
    fun preloadAd(placement: AdPlacement)

    /**
     * Shows a banner ad.
     */
    fun showBanner(
        onLoaded: () -> Unit,
        onFailed: (String) -> Unit
    )

    /**
     * Hides and tears down any active banner ad view.
     */
    fun hideBanner()

    /**
     * Shows a full-screen interstitial ad.
     */
    fun showInterstitial(
        onDismissed: () -> Unit,
        onFailed: (String) -> Unit
    )

    /**
     * Shows an opt-in rewarded video ad.
     */
    fun showRewarded(
        placement: AdPlacement,
        onRewarded: (AdReward) -> Unit,
        onClosed: () -> Unit,
        onFailed: (String) -> Unit
    )
}
