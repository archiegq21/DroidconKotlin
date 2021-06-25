package co.touchlab.sessionize.util

fun interface SoftExceptionHandler {
    fun handle(e: Throwable, message: String)
}