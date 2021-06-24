package co.touchlab.sessionize

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.freeze
import kotlin.reflect.KProperty

object ServiceRegistry {

    var timeZone: String by FrozenDelegate()

    var softExceptionCallback: ((e: Throwable, message: String) -> Unit) by FrozenDelegate()

    fun initServiceRegistry(
        timeZone: String
    ) {
        this.timeZone = timeZone
    }

    fun initLambdas(
        softExceptionCallback: (e: Throwable, message: String) -> Unit
    ) {
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