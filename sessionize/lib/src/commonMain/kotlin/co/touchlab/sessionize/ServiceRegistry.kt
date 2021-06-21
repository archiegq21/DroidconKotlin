package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.api.SessionizeApiImpl
import co.touchlab.sessionize.platform.backgroundDispatcher
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.freeze
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KProperty

object ServiceRegistry {
    var analyticsApi: AnalyticsApi by FrozenDelegate()

    var notificationsApi: NotificationsApi by FrozenDelegate()
    var coroutinesDispatcher: CoroutineDispatcher by FrozenDelegate()
    var backgroundDispatcher: CoroutineDispatcher by FrozenDelegate()
    var appSettings: Settings by FrozenDelegate()
    var timeZone: String by FrozenDelegate()

    var staticFileLoader: ((filePrefix: String, fileType: String) -> String?) by FrozenDelegate()
    var clLogCallback: ((s: String) -> Unit) by FrozenDelegate()
    var softExceptionCallback: ((e: Throwable, message: String) -> Unit) by FrozenDelegate()

    fun initServiceRegistry(
        settings: Settings,
        analyticsApi: AnalyticsApi,
        notificationsApi: NotificationsApi,
        timeZone: String
    ) {
        coroutinesDispatcher = Dispatchers.Main
        backgroundDispatcher = backgroundDispatcher()
        appSettings = settings
        this.analyticsApi = analyticsApi
        this.notificationsApi = notificationsApi
        appSettings = settings
        this.timeZone = timeZone
    }

    fun initLambdas(
        staticFileLoader: (filePrefix: String, fileType: String) -> String?,
        clLogCallback: (s: String) -> Unit,
        softExceptionCallback: (e: Throwable, message: String) -> Unit
    ) {
        this.staticFileLoader = staticFileLoader
        this.clLogCallback = clLogCallback
        this.softExceptionCallback = softExceptionCallback
    }
}

internal class FrozenDelegate<T> {
    private val delegateReference = AtomicReference<T?>(null)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = delegateReference.get()!!

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        delegateReference.set(value.freeze())
    }
}

internal class ThreadLocalDelegate<T> {
    private val delegateReference = ThreadLocalRef<T?>()
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = delegateReference.get()!!

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        delegateReference.set(value)
    }
}