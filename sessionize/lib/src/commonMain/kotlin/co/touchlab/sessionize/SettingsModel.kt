package co.touchlab.sessionize

import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.platform.NotificationsModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SettingsModel : BaseModel(ServiceRegistry.coroutinesDispatcher), KoinComponent {

    private val settings: Settings by lazy { get() }

    val feedbackEnabled: Boolean
        get() = settings.getBoolean(FEEDBACK_ENABLED, true)

    val remindersEnabled: Boolean
        get() = settings.getBoolean(REMINDERS_ENABLED, true)

    fun setRemindersSettingEnabled(enabled: Boolean) = mainScope.launch {
        NotificationsModel.remindersEnabled = enabled

        if (enabled && !NotificationsModel.notificationsEnabled) {
            ServiceRegistry.notificationsApi.initializeNotifications {
                mainScope.launch {
                    NotificationsModel.recreateReminderNotifications()
                }
            }
        } else {
            NotificationsModel.recreateReminderNotifications()
        }
    }

    fun setFeedbackSettingEnabled(enabled: Boolean) = mainScope.launch {
        NotificationsModel.feedbackEnabled = enabled

        if (enabled && !NotificationsModel.notificationsEnabled) {
            ServiceRegistry.notificationsApi.initializeNotifications {
                mainScope.launch {
                    NotificationsModel.recreateFeedbackNotifications()
                }
            }
        } else {
            NotificationsModel.recreateFeedbackNotifications()
        }
    }
}