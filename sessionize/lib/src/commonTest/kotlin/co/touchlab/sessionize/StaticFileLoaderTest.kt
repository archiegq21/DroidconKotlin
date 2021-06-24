package co.touchlab.sessionize


import co.touchlab.sessionize.file.FileLoader
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

abstract class StaticFileLoaderTest {

    abstract val fileLoader: FileLoader

    fun setUp() {
        ServiceRegistry.initServiceRegistry(
//            testDbConnection(),
//            TestSettings(),
//            SessionizeApiMock(),
//            AnalyticsApiMock(),
//            NotificationsApiMock(),
            "-0400"
        )
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun testAbout() {
        val about = fileLoader.load("about", "json")
        about?.let {

            val aboutJson = Json {
                allowStructuredMapKeys = true
                ignoreUnknownKeys = true
            }.parseToJsonElement(it).jsonArray
            assertNotEquals(aboutJson.size, 0, "empty about.json or none found")
            assertTrue(aboutJson[0].jsonObject.containsKey("icon"))
            assertTrue(aboutJson[0].jsonObject.containsKey("title"))
            assertTrue(aboutJson[0].jsonObject.containsKey("detail"))

        }
    }

    @Test
    fun testSchedule() {
        val schedule = fileLoader.load("schedule", "json")
        schedule?.let {
            val scheduleJson = Json {
                allowStructuredMapKeys = true
                ignoreUnknownKeys = true
            }.parseToJsonElement(it).jsonArray
            assertNotEquals(scheduleJson.size, 0, "empty schedule.json or none found")
            assertTrue(scheduleJson[0].jsonObject.containsKey("date"))
            assertTrue(scheduleJson[0].jsonObject.containsKey("rooms"))
        }
    }

    @Test
    fun testSpeakers() {
        val speakers = fileLoader.load("speakers", "json")
        speakers?.let {
            val speakersJson = Json {
                allowStructuredMapKeys = true
                ignoreUnknownKeys = true
            }.parseToJsonElement(it).jsonArray
            assertNotEquals(speakersJson.size, 0, "empty speakers.json or none found")
            assertTrue(speakersJson[0].jsonObject.containsKey("id"))
            assertTrue(speakersJson[0].jsonObject.containsKey("firstName"))
            assertTrue(speakersJson[0].jsonObject.containsKey("lastName"))
        }
    }

}

