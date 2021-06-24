package co.touchlab.sessionize.util

interface SoftExceptionHandler {
    fun handle(e: Throwable, message: String)
}