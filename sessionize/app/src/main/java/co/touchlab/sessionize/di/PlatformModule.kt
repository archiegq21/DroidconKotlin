package co.touchlab.sessionize.di

import co.touchlab.droidcon.db.DroidconDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val platformModule = module {

    single<SqlDriver> {
        AndroidSqliteDriver(DroidconDb.Schema, androidContext(), "droidcondb2")
    }

}