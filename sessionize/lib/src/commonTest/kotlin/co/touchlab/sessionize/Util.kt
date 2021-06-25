package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.di.initKoin
import co.touchlab.sessionize.file.FileLoader
import co.touchlab.sessionize.mocks.NotificationsApiMock
import co.touchlab.sessionize.util.LogHandler
import co.touchlab.sessionize.util.SoftExceptionHandler
import org.koin.dsl.module

fun initTestKoin(
    timeZone: String,
) {
    initKoin(
        platformModule = module {
            single<SessionizeApi>(override = true) {
                SessionizeApiMock()
            }

            single<AnalyticsApi>(override = true) {
                AnalyticsApiMock()
            }

            single<NotificationsApi>(override = true) {
                NotificationsApiMock()
            }

            single(override = true) {
                testDbConnection()
            }

            single(override = true) {
                TestSettings()
            }

            single(override = true) {
                FileLoader { filePrefix, _ ->
                    when (filePrefix) {
                        "sponsors" -> SPONSORS
                        "speakers" -> SPEAKERS
                        "schedule" -> SCHEDULE
                        else -> SCHEDULE
                    }
                }
            }

            single(override = true) {
                LogHandler { s: String -> }
            }

            single(override = true) {
                SoftExceptionHandler { e: Throwable, message: String ->
                    println(message)
                }
            }
        }
    )

    TimeZoneProvider.init(timeZone)
    AppContext.initAppContext()
}