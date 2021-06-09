package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.sessionize.api.AnalyticsApi
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSDate
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.Foundation.NSURLConnection
import platform.Foundation.NSURLRequest
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.sendSynchronousRequest
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

internal actual val mainThread: Boolean
    get() = NSThread.isMainThread

actual fun createUuid(): String = NSUUID.UUID().UUIDString


@Suppress("unused")
fun defaultDriver(): SqlDriver = NativeSqliteDriver(DroidconDb.Schema, "sessionizedb")

@Suppress("unused")
fun defaultSettings(): Settings = AppleSettings.Factory().create("DROIDCON_SETTINGS")

private fun getDirPath(folder: String): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true);
    val documentsDirectory = paths[0] as String;

    val databaseDirectory = documentsDirectory + "/$folder"

    val fileManager = NSFileManager.defaultManager()

    if (!fileManager.fileExistsAtPath(databaseDirectory))
        fileManager.createDirectoryAtPath(databaseDirectory, true, null, null); //Create folder

    return databaseDirectory
}

private fun getDatabaseDirPath(): String = getDirPath("databases")

@Suppress("unused")
fun createAnalyticsApiImpl(analyticsCallback: (name: String, params: Map<String, Any>) -> Unit): AnalyticsApi {
    return object: AnalyticsApi {
        override fun logEvent(name: String, params: Map<String, Any>) {
            analyticsCallback(name, params)
        }
    }
}

fun forceInclude() = listOf(CoroutineDispatcher::class)
actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

actual fun backgroundDispatcher():CoroutineDispatcher = Dispatchers.Default //newFixedThreadPoolContext(1, "arst")