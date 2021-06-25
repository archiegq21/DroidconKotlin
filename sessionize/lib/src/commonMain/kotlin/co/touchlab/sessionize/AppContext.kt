package co.touchlab.sessionize

import co.touchlab.sessionize.SettingsKeys.KEY_FIRST_RUN
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.file.FileLoader
import co.touchlab.sessionize.file.FileRepo
import co.touchlab.sessionize.platform.NotificationsModel
import co.touchlab.sessionize.platform.printThrowable
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object AppContext: KoinComponent {

    private val mainScope = MainScope()

    private val fileLoader: FileLoader by inject()

    private val settings: Settings by inject()

    private val notificationsApi: NotificationsApi by inject()

    fun initAppContext(
        networkRepo: NetworkRepo = NetworkRepo,
        fileRepo: FileRepo = FileRepo,
        dbHelper: SessionizeDbHelper = SessionizeDbHelper,
        notificationsModel: NotificationsModel = NotificationsModel
    ) {
        dbHelper.initDatabase(get())

        notificationsApi.initializeNotifications { success ->
            if (success) {
                mainScope.launch {
                    notificationsModel.createNotifications()
                }
            } else {
                notificationsModel.cancelNotifications()
            }
        }

        mainScope.launch {
            maybeLoadSeedData(settings, fileRepo)
            networkRepo.refreshData()
        }
    }

    private suspend fun maybeLoadSeedData(settings: Settings, fileRepo: FileRepo) =
        withContext(Dispatchers.Default) {
            try {
                if (firstRun(settings)) {
                    fileRepo.seedFileLoad(fileLoader)
                    updateFirstRun(settings)
                }
            } catch (e: Exception) {
                printThrowable(e)
            }
        }

    private fun firstRun(settings: Settings): Boolean =
        settings.getBoolean(KEY_FIRST_RUN, true)

    private fun updateFirstRun(settings: Settings) {
        settings.putBoolean(KEY_FIRST_RUN, false)
    }
}
