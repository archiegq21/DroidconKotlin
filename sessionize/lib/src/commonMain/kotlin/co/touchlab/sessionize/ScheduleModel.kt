package co.touchlab.sessionize

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.display.DaySchedule
import co.touchlab.sessionize.display.convertMapToDaySchedule
import co.touchlab.sessionize.display.formatHourBlocks
import co.touchlab.sessionize.util.LogHandler
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Data model for schedule. Configure live data instances.
 */
class ScheduleModel(private val allEvents: Boolean) :
    BaseQueryModelView<SessionWithRoom, List<DaySchedule>>(
        SessionizeDbHelper.getSessionsQuery(),
        {
            val dbSessions = it.executeAsList()
            val sessions = if (allEvents) {
                dbSessions
            } else {
                dbSessions.filter { it.rsvp != 0L }
            }

            val hourBlocks = formatHourBlocks(sessions)
            convertMapToDaySchedule(hourBlocks)
        },
        Dispatchers.Main
    ), KoinComponent {

    private val logHandler: LogHandler by inject()

    init {
        logHandler.log("init ScheduleModel()")
    }

    fun register(view: ScheduleView) {
        super.register(view)
    }

    interface ScheduleView : View<List<DaySchedule>>
}