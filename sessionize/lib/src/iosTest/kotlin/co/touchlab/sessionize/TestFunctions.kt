package co.touchlab.sessionize

import co.touchlab.droidcon.db.DroidconDb
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import kotlinx.coroutines.runBlocking

actual fun testDbConnection(): SqlDriver = NativeSqliteDriver(DroidconDb.Schema, "sessionizedb")

actual fun <T> runTest(block: suspend () -> T) {
    runBlocking { block() }
}

fun accessServiceRegistry() = ServiceRegistry