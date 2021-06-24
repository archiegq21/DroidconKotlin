package co.touchlab.sessionize

import co.touchlab.sessionize.db.coroutines.asFlow
import co.touchlab.sessionize.platform.assertNotMainThread
import co.touchlab.sessionize.platform.printThrowable
import co.touchlab.sessionize.util.SoftExceptionHandler
import co.touchlab.stately.ensureNeverFrozen
import com.squareup.sqldelight.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

/**
 * Sort of a "controller" in MVC thinking. Pass in the SQLDelight Query,
 * a method to actually extract data on updates. The View interface is
 * generically defined to take data extracted from the Query, and manage
 * registering and shutting down.
 */
abstract class BaseQueryModelView<Q : Any, VT>(
    query: Query<Q>,
    extractData: (Query<Q>) -> VT,
    mainContext: CoroutineContext
) : BaseModel(mainContext), KoinComponent {

    protected val exceptionHandler: SoftExceptionHandler by inject()

    init {
        ensureNeverFrozen()
        mainScope.launch {
            query.asFlow()
                .map {
                    assertNotMainThread()
                    extractData(it)
                }
                .flowOn(Dispatchers.Default)
                .collect { vt ->
                    view?.let {
                        it.update(vt)
                    }
                }

        }
    }

    private var view: View<VT>? = null

    fun register(view: View<VT>) {
        this.view = view
    }

    fun shutDown() {
        view = null
        onDestroy()
    }

    interface View<VT> {
        suspend fun update(data: VT)
        fun error(exceptionHandler: SoftExceptionHandler, t: Throwable) {
            printThrowable(t)
            exceptionHandler.handle(t, t.message ?: "(Unknown View Error)")
        }
    }
}