package game.ludora

import android.app.Application
import com.google.android.gms.ads.MobileAds

class LudoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
    }
}
