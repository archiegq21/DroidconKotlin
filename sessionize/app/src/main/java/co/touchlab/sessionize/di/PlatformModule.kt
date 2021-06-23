package co.touchlab.sessionize.di

import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.sessionize.AnalyticsApiImpl
import co.touchlab.sessionize.NotificationsApiImpl
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.NotificationsApi
import com.google.firebase.analytics.FirebaseAnalytics
import com.russhwolf.settings.AndroidSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun androidModule() = module {

    single<AnalyticsApi> {
        AnalyticsApiImpl(FirebaseAnalytics.getInstance(androidContext()))
    }

    single<NotificationsApi> {
        NotificationsApiImpl()
    }

    single<SqlDriver> {
        AndroidSqliteDriver(DroidconDb.Schema, androidContext(), "droidcondb2")
    }

    single {
        AndroidSettings.Factory(androidContext()).create("DROIDCON_SETTINGS2")
    }

}