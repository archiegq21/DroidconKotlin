package co.touchlab.sessionize.di

import co.touchlab.droidcon.db.DroidconDb
import com.russhwolf.settings.AppleSettings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.dsl.module
import kotlin.native.concurrent.freeze

val platformModule = module {

    single<SqlDriver> {
        NativeSqliteDriver(DroidconDb.Schema, "sessionizedb")
    }

    single {
        AppleSettings.Factory().create("DROIDCON_SETTINGS")
    }

}