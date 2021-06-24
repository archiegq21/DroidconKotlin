package co.touchlab.sessionize.platform

import android.util.Log
import co.touchlab.sessionize.util.SoftExceptionHandler

object SoftExceptionHandlerImpl : SoftExceptionHandler {
    override fun handle(e: Throwable, message: String) {
        Log.e("MainApp", message, e)
    }
}