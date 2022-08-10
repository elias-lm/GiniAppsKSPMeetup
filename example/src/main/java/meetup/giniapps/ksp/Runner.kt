package meetup.giniapps.ksp

import androidx.compose.ui.text.toLowerCase
import meetup.giniapps.eklientannotaions.EKlient
import meetup.giniapps.eklientannotaions.EKlientGet
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@EKlient("https://my-json-server.typicode.com/")
@EKlientGet(path = "elias-lm/GiniAppsKSPMeetup/db", functionName = "getGiniDB")
interface GiniAPI


suspend fun main() {
    val giniApi: GiniAPI by GiniAPIEKlientFactory()

    giniApi {
    }
}