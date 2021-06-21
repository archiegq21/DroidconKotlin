package co.touchlab.sessionize.api

import co.touchlab.sessionize.BaseModel
import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.jsondata.Days
import co.touchlab.sessionize.jsondata.Speaker
import co.touchlab.sessionize.jsondata.SponsorSessionGroup
import co.touchlab.sessionize.platform.NotificationsModel.createNotifications
import co.touchlab.sessionize.platform.NotificationsModel.notificationsEnabled
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.platform.printThrowable
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal


@ThreadLocal
object NetworkRepo : KoinComponent {

    private val api: SessionizeApi by lazy { get() }

    private val settings: Settings by lazy { get() }

    fun dataCalls() = CoroutineScope(ServiceRegistry.coroutinesDispatcher).mainScope.launch {
        try {
            val networkSpeakers = api.getSpeakers()
            val networkSessions = api.getSessions()
            val networkSponsorSessions = api.getSponsorSession()

            callPrimeAll(networkSpeakers, networkSessions, networkSponsorSessions)

            //If we do some kind of data re-load after a user logs in, we'll need to update this.
            //We assume for now that when the app first starts, you have nothing rsvp'd
            if (notificationsEnabled) {
                createNotifications()
            }
        } catch (e: Exception) {
            printThrowable(e)
        }
    }

    internal suspend fun callPrimeAll(
        speakers: List<Speaker>,
        schedules: List<Days>,
        sponsorSessions: List<SponsorSessionGroup>
    ) = withContext(ServiceRegistry.backgroundDispatcher) {
        SessionizeDbHelper.primeAll(
            speakers,
            schedules,
            sponsorSessions
        )
        settings.putLong(SettingsKeys.KEY_LAST_LOAD, currentTimeMillis())
    }

    fun refreshData() {
        if (!settings.getBoolean(SettingsKeys.KEY_FIRST_RUN, true)) {
            val lastLoad = settings.getLong(SettingsKeys.KEY_LAST_LOAD)
            if (lastLoad < (currentTimeMillis() - (Durations.TWO_HOURS_MILLIS.toLong()))) {
                dataCalls()
            }
        }
    }

    fun sendFeedback() = CoroutineScope(ServiceRegistry.coroutinesDispatcher).mainScope.launch {
        try {
            SessionizeDbHelper.sendFeedback()
        } catch (e: Throwable) {
            ServiceRegistry.softExceptionCallback(e, "Feedback Send Failed")
        }
    }

    private class CoroutineScope(mainContext: CoroutineContext) : BaseModel(mainContext)
}