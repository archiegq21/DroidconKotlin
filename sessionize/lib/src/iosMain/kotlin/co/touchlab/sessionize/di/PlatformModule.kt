package co.touchlab.sessionize.di

import co.touchlab.droidcon.db.DroidconDb
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.dsl.module

val platformModule = module {

    single<SqlDriver> {
        NativeSqliteDriver(DroidconDb.Schema, "sessionizedb")
    }

}