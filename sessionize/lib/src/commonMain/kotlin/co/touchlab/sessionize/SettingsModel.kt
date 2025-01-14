package co.touchlab.sessionize

import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.NotificationsModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsModel : BaseModel(Dispatchers.Main), KoinComponent {

    private val settings: Settings by inject()

    private val notificationsApi: NotificationsApi by inject()

    val feedbackEnabled: Boolean
        get() = settings.getBoolean(FEEDBACK_ENABLED, true)

    val remindersEnabled: Boolean
        get() = settings.getBoolean(REMINDERS_ENABLED, true)

    fun setRemindersSettingEnabled(enabled: Boolean) = mainScope.launch {
        NotificationsModel.remindersEnabled = enabled

        if (enabled && !NotificationsModel.notificationsEnabled) {
            notificationsApi.initializeNotifications {
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
            notificationsApi.initializeNotifications {
                mainScope.launch {
                    NotificationsModel.recreateFeedbackNotifications()
                }
            }
        } else {
            NotificationsModel.recreateFeedbackNotifications()
        }
    }
}