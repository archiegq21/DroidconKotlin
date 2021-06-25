package co.touchlab.sessionize.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(
    platformModule: Module,
    additionalSetUp: KoinApplication.() -> Unit = {},
) = startKoin {
    modules(
        platformModule,
        commonModule,
    )
    additionalSetUp()
}