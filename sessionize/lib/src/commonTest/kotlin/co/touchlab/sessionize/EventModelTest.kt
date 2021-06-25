package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.db.DateAdapter
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.di.initKoin
import co.touchlab.sessionize.file.FileLoader
import co.touchlab.sessionize.jsondata.Days
import co.touchlab.sessionize.jsondata.Speaker
import co.touchlab.sessionize.jsondata.SponsorSessionGroup
import co.touchlab.sessionize.mocks.FeedbackApiMock
import co.touchlab.sessionize.mocks.NotificationsApiMock
import co.touchlab.sessionize.util.LogHandler
import co.touchlab.sessionize.util.SoftExceptionHandler
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

abstract class EventModelTest: KoinTest {
    private val sessionizeApiMock = SessionizeApiMock()
    private val analyticsApiMock = AnalyticsApiMock()
    private val notificationsApiMock = NotificationsApiMock()
    //private val feedbackApiMock = FeedbackApiMock()

    private val timeZone = "-0800"

    @BeforeTest
    fun setup() {
        initTestKoin("-0800")
    }

    @Test
    fun testRsvpAndAnalytics() = runTest {
        val eventModel = EventModel("67316")
        val sessions = SessionizeDbHelper.sessionQueries.allSessions().executeAsList()
        if (sessions.isNotEmpty()) {
            val session = sessions.first()
            val si = collectSessionInfo(session)
            eventModel.toggleRsvpSuspend(si)
            assertTrue { sessionizeApiMock.rsvpCalled }
            assertTrue { analyticsApiMock.logCalled }
            assertTrue { notificationsApiMock.reminderCalled.value }
        }
    }

    /*@Test
    fun testFeedbackModel() = runTest {
        val fbModel = feedbackApiMock.getFeedbackModel()
        fbModel.showFeedbackForPastSessions(feedbackApiMock)

        assertTrue { feedbackApiMock.feedbackError != null }
    }*/

    @Test
    fun testPSTTimeZoneCorrect() {
        val timeStr = "2019-04-12T08:00:00"
        val correctMillis = 1555084800000

        val timeStrWithZone = timeStr + timeZone

        val dateAdapter = DateAdapter()
        val timeDate = dateAdapter.decode(timeStrWithZone)
        val newTimeStr = dateAdapter.encode(timeDate)

        assertTrue { newTimeStr == timeStr }
        //assertTrue { timeDate.toLongMillis() == correctMillis }
    }
}

class AnalyticsApiMock : AnalyticsApi {
    var logCalled = false

    override fun logEvent(name: String, params: Map<String, Any>) {
        logCalled = true
    }
}

class SessionizeApiMock : SessionizeApi {
    override suspend fun sendFeedback(sessionId: String, rating: Int, comment: String?): Boolean {
        return true
    }

    var rsvpCalled = false
    override suspend fun getSpeakers(): List<Speaker> {
        return emptyList()
    }

    override suspend fun getSessions(): List<Days> {
        return emptyList()
    }

    override suspend fun getSponsorSession(): List<SponsorSessionGroup> {
        return emptyList()
    }

    override suspend fun recordRsvp(methodName: String, sessionId: String): Boolean {
        rsvpCalled = true
        return true
    }
}