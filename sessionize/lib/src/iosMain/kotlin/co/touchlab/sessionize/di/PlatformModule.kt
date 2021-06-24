package co.touchlab.sessionize.di

import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.file.FileLoader
import co.touchlab.sessionize.platform.FileLoaderImpl
import co.touchlab.sessionize.platform.createAnalyticsApiImpl
import com.russhwolf.settings.AppleSettings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.dsl.module
import platform.Foundation.NSBundle

fun iosModule(
    analyticsCallback: (name: String, params: Map<String, Any>) -> Unit,
    notificationsApi: NotificationsApi,
) = module {

    single {
        createAnalyticsApiImpl(analyticsCallback)
    }

    single {
        notificationsApi
    }

    single<SqlDriver> {
        NativeSqliteDriver(DroidconDb.Schema, "sessionizedb")
    }

    single {
        AppleSettings.Factory().create("DROIDCON_SETTINGS")
    }

    single<FileLoader> {
        FileLoaderImpl
    }

}