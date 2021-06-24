package co.touchlab.sessionize

import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.db.SessionizeDbHelper.sponsorSessionQueries
import co.touchlab.sessionize.db.SessionizeDbHelper.userAccountQueries
import co.touchlab.sessionize.jsondata.Sponsor
import co.touchlab.sessionize.platform.printThrowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.set
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object SponsorSessionModel : BaseModel(Dispatchers.Main), KoinComponent {

    private val analyticsApi: AnalyticsApi by inject()

    //This is super ugly and I apologize, but the changes weren't finished in time and I need to release...
    var sponsor: Sponsor? by FrozenDelegate()

    /*init {
        sponsor?.let {
            ServiceRegistry.clLogCallback("init SponsorSessionModel(${it.sponsorId})")
            ServiceRegistry.analyticsApi.logEvent("sponsor_detail", mapOf(Pair("sponsorId", it.sponsorId
                    ?: ""), Pair("groupName", it.groupName)))
        }
    }*/

    fun loadSponsorDetail(proc: (SponsorSessionInfo) -> Unit, error: (Throwable) -> Unit) {
        val sponsorArg = sponsor
        if (sponsorArg?.sponsorId != null) {
            mainScope.launch {
                try {
                    val dataPair = loadSponsorDetailData(sponsorArg)

                    proc(SponsorSessionInfo(sponsorArg, dataPair.first, dataPair.second))
                } catch (e: Exception) {
                    error(e)
                }
            }
        } else {
            error(IllegalStateException("Sponsor info not sound"))
        }
    }

    internal suspend fun loadSponsorDetailData(sponsor: Sponsor): Pair<String, List<UserAccount>> =
        withContext(Dispatchers.Default) {
            val id = sponsor.sponsorId!!
            Pair(
                sponsorSessionQueries.sponsorSessionById(id).executeAsOne().description ?: "",
                userAccountQueries.selectBySession(id).executeAsList()
            )
        }

    private suspend fun sendAnalytics() {

        try {
            sponsor?.let {
                val params = HashMap<String, Any>()
                params["sponsorId"] = it.sponsorId ?: ""
                params["name"] = it.name

                analyticsApi.logEvent("SPONSOR_VIEWED", params)
            }

        } catch (e: Exception) {
            printThrowable(e)
        }
    }

    interface View<VT> {
        suspend fun update(data: VT)
        fun error(t: Throwable) {
            printThrowable(t)
            ServiceRegistry.softExceptionCallback(t, t.message ?: "(Unknown View Error)")
        }
    }
}

data class SponsorSessionInfo(
    val sponsor: Sponsor,
    val sessionDetail: String,
    val speakers: List<UserAccount>
)
