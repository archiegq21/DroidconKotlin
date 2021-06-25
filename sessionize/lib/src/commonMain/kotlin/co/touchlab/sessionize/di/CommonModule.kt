package co.touchlab.sessionize.di

import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.api.SessionizeApiImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.native.concurrent.SharedImmutable

val commonModule  = module {

    single<SessionizeApi> {
        SessionizeApiImpl
    }

}