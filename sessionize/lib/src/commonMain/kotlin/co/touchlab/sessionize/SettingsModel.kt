package co.touchlab.sessionize

import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.platform.NotificationsModel
import kotlinx.coroutines.launch

class SettingsModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {

    val feedbackEnabled: Boolean
        get() = ServiceRegistry.appSettings.getBoolean(FEEDBACK_ENABLED, true)

    val remindersEnabled: Boolean
        get() = ServiceRegistry.appSettings.getBoolean(REMINDERS_ENABLED, true)

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