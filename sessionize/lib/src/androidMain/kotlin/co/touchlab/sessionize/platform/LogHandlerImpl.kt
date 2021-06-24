package co.touchlab.sessionize.platform

import android.util.Log
import co.touchlab.sessionize.util.LogHandler

object LogHandlerImpl : LogHandler {
    override fun log(s: String) {
        Log.w("MainApp", s)
    }
}