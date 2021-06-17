package co.touchlab.sessionize.di

import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.api.SessionizeApiImpl
import org.koin.dsl.module

val commonModule = module {

    single<SessionizeApi> {
        SessionizeApiImpl
    }

}