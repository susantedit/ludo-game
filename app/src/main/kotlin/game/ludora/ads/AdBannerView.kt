package game.ludora.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import game.ludora.BuildConfig

/**
 * Jetpack Compose wrapper for Google Mobile Ads Banner.
 *
 * Uses [BuildConfig.BANNER_AD_ID], which resolves to:
 * - Google's official test banner ID in debug builds.
 * - The real user-configured banner ID in release builds.
 */
@Composable
fun AdBannerView(
    modifier: Modifier = Modifier,
    adUnitId: String = BuildConfig.BANNER_AD_ID,
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    AndroidView(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        onAdLoaded()
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        onAdFailed(error)
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
