package co.touchlab.sessionize.file

import co.touchlab.sessionize.db.SessionizeDbHelper
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object FileRepo {

    private val json = Json {
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true

        // TODO: Revisit once https://github.com/Kotlin/kotlinx.serialization/issues/1450#issuecomment-841214332
        //  gets resolved. As a workaround, disabling `useAlternativeNames` solves the issue.
        useAlternativeNames = false
    }

    fun seedFileLoad(fileLoader: FileLoader) {
        val speakerJson = fileLoader.load("speakers", "json")
        val scheduleJson = fileLoader.load("schedule", "json")
        val sponsorSessionJson = fileLoader.load("sponsor_session", "json")

        if (speakerJson != null && scheduleJson != null && sponsorSessionJson != null) {
            SessionizeDbHelper.primeAll(
                json.decodeFromString(speakerJson),
                json.decodeFromString(scheduleJson),
                json.decodeFromString(sponsorSessionJson)
            )
        } else {
            //This should only ever happen in dev
            throw NullPointerException("Couldn't load static files")
        }
    }


}