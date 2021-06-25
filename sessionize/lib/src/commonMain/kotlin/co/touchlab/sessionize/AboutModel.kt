package co.touchlab.sessionize

import co.touchlab.sessionize.file.FileLoader
import co.touchlab.sessionize.util.LogHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object AboutModel : BaseModel(Dispatchers.Main), KoinComponent {

    private val fileLoader: FileLoader by inject()

    private val logHandler: LogHandler by inject()

    fun loadAboutInfo(proc: (aboutInfo: List<AboutInfo>) -> Unit) = mainScope.launch {
        logHandler.log("loadAboutInfo AboutModel()")
        proc(aboutLoad(fileLoader))
    }

    private suspend fun aboutLoad(
        fileLoader: FileLoader
    ): List<AboutInfo> = withContext(Dispatchers.Default) {
        val aboutJsonString = fileLoader.load("about", "json")!!
        Json.decodeFromString(aboutJsonString)
    }
}

@Serializable
data class AboutInfo(val icon: String, val title: String, val detail: String)