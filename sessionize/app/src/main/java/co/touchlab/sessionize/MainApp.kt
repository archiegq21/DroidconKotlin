package co.touchlab.sessionize

import android.app.Application
import android.util.Log
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.di.androidModule
import co.touchlab.sessionize.di.initKoin
import co.touchlab.sessionize.platform.AndroidAppContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin

class MainApp : Application() {

    private lateinit var koin: Koin

    private val notificationsApi: NotificationsApi by lazy { koin.get() }

    override fun onCreate() {
        super.onCreate()

        TimeZoneProvider.init(BuildConfig.TIME_ZONE)

        koin = initKoin(
            platformModule = androidModule(),
        ) {
            androidContext(this@MainApp)
        }.koin

        AndroidAppContext.app = this

        AppContext.initAppContext()

        NetworkRepo.sendFeedback()

        @Suppress("ConstantConditionIf")
        if (BuildConfig.FIREBASE_ENABLED) {
            //FirebaseMessageHandler.init()
        } else {
            Log.d("MainApp", "Firebase json not found: Firebased Not Enabled")
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        notificationsApi.deinitializeNotifications()
    }

}
