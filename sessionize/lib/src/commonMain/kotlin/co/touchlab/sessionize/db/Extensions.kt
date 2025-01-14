package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.Room
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.db.SessionizeDbHelper.roomQueries
import co.touchlab.sessionize.db.SessionizeDbHelper.sessionQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun SessionWithRoom.isBlock(): Boolean = this.serviceSession != 0L
fun SessionWithRoom.isRsvp(): Boolean = this.rsvp != 0L

/**
 * Provide for "ORM-like" associated query
 */
internal suspend fun UserAccount.sessions(): List<Session> =
    withContext(Dispatchers.Default) {
        sessionQueries.userSessions(id).executeAsList()
    }

internal suspend fun Session.room(): Room = withContext(Dispatchers.Default) {
    roomQueries.selectById(roomId!!).executeAsOne()
}