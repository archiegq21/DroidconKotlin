package co.touchlab.sessionize.platform

import co.touchlab.sessionize.TimeZoneProvider
import org.koin.core.component.KoinComponent
import platform.Foundation.*
import kotlin.math.floor

actual class Date(val iosDate: NSDate) {
    actual fun toLongMillis(): Long {
        return floor(iosDate.timeIntervalSince1970).toLong() * 1000L
    }
}

actual class DateFormatHelper actual constructor(format: String): KoinComponent {

    private val dateFormatterConference: NSDateFormatter = NSDateFormatter().apply {
        this.timeZone = NSTimeZone.timeZoneWithName(TimeZoneProvider.timeZone)!!
        this.dateFormat = format
    }

    private val dateFormatterLocal: NSDateFormatter = NSDateFormatter().apply {
        this.timeZone = NSTimeZone.defaultTimeZone
        this.dateFormat = format

    }

    actual fun toConferenceDate(s: String): Date = Date(dateFormatterConference.dateFromString(s)!!)
    actual fun toLocalDate(s: String): Date = Date(dateFormatterLocal.dateFromString(s)!!)
    actual fun formatConferenceTZ(d: Date): String =
        dateFormatterConference.stringFromDate(d.iosDate)

    actual fun formatLocalTZ(d: Date): String = dateFormatterLocal.stringFromDate(d.iosDate)
}
