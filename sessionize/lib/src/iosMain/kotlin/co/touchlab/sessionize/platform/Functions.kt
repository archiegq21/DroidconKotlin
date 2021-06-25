package co.touchlab.sessionize.platform

import co.touchlab.sessionize.api.AnalyticsApi
import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.*

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

internal actual val mainThread: Boolean
    get() = NSThread.isMainThread

actual fun createUuid(): String = NSUUID.UUID().UUIDString

private fun getDirPath(folder: String): String {
    val paths =
        NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true);
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
    return object : AnalyticsApi {
        override fun logEvent(name: String, params: Map<String, Any>) {
            analyticsCallback(name, params)
        }
    }
}

fun forceInclude() = listOf(CoroutineDispatcher::class)
actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}