package co.touchlab.sessionize

import co.touchlab.sessionize.SettingsKeys.KEY_FIRST_RUN
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.file.FileRepo
import co.touchlab.sessionize.platform.NotificationsModel
import co.touchlab.sessionize.platform.backgroundDispatcher
import co.touchlab.sessionize.platform.printThrowable
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object AppContext: KoinComponent {

    private val mainScope = MainScope()

    private val settings: Settings by lazy { get() }

    fun initAppContext(
        networkRepo: NetworkRepo = NetworkRepo,
        fileRepo: FileRepo = FileRepo,
        serviceRegistry: ServiceRegistry = ServiceRegistry,
        dbHelper: SessionizeDbHelper = SessionizeDbHelper,
        notificationsModel: NotificationsModel = NotificationsModel
    ) {
        dbHelper.initDatabase(get())

        serviceRegistry.notificationsApi.initializeNotifications { success ->
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
        withContext(/*mainScope.coroutineContext*/ServiceRegistry.backgroundDispatcher) {
            try {
                if (firstRun(settings)) {
                    fileRepo.seedFileLoad()
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

    val backgroundContext = backgroundDispatcher()
}
