package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.LOCAL_NOTIFICATIONS_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.db.SessionizeDbHelper.sessionQueries
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object NotificationsModel: KoinComponent {

    private val settings: Settings by lazy { get() }

    // Settings
    var notificationsEnabled: Boolean
        get() = settings.getBoolean(LOCAL_NOTIFICATIONS_ENABLED, true)
        set(value) {
            settings[LOCAL_NOTIFICATIONS_ENABLED] = value
        }

    var feedbackEnabled: Boolean
        get() = settings.getBoolean(FEEDBACK_ENABLED, true)
        set(value) {
            settings[FEEDBACK_ENABLED] = value
        }

    var remindersEnabled: Boolean
        get() = settings.getBoolean(LOCAL_NOTIFICATIONS_ENABLED, true) &&
                settings.getBoolean(REMINDERS_ENABLED, true)
        set(value) {
            settings[REMINDERS_ENABLED] = value
        }

    suspend fun createNotifications() {
        if (notificationsEnabled) {
            recreateReminderNotifications()
            recreateFeedbackNotifications()
        }
    }

    fun cancelNotifications() {
        cancelReminderNotifications(true)
        cancelFeedbackNotifications()
    }

    fun cancelFeedbackNotifications() =
        ServiceRegistry.notificationsApi.cancelFeedbackNotifications()

    fun cancelReminderNotifications(andDismissals: Boolean) =
        ServiceRegistry.notificationsApi.cancelReminderNotifications(andDismissals)

    suspend fun recreateReminderNotifications() {
        cancelReminderNotifications(false)
        if (remindersEnabled) {
            val mySessions = mySessions()
            if (mySessions.isNotEmpty()) {
                ServiceRegistry.notificationsApi.scheduleReminderNotificationsForSessions(mySessions)
            }
        }
    }

    suspend fun recreateFeedbackNotifications() {
        cancelFeedbackNotifications()
        if (feedbackEnabled) {
            val mySessions = mySessions()
            if (mySessions.isNotEmpty()) {
                ServiceRegistry.notificationsApi.scheduleFeedbackNotificationsForSessions(mySessions)
            }
        }
    }

    private suspend fun mySessions(): List<MySessions> =
        withContext(ServiceRegistry.backgroundDispatcher) {
            sessionQueries.mySessions().executeAsList()
        }

    fun getReminderTimeFromSession(session: MySessions): Long =
        session.startsAt.toLongMillis() - Durations.TEN_MINS_MILLIS

    fun getReminderNotificationTitle(session: MySessions) = "Upcoming Event in ${session.roomName}"
    fun getReminderNotificationMessage(session: MySessions) = "${session.title} is starting soon."
    fun getFeedbackTimeFromSession(session: MySessions): Long =
        session.endsAt.toLongMillis() + Durations.TEN_MINS_MILLIS

    fun getFeedbackNotificationTitle() = "Feedback Time!"
    fun getFeedbackNotificationMessage() = "Your Feedback is Requested"
}