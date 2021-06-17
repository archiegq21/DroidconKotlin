package co.touchlab.sessionize.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(
    additionalSetUp: KoinApplication.() -> Unit = {},
) = startKoin {
    modules(
        commonModule,
    )
    additionalSetUp()
}