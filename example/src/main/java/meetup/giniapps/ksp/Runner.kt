package meetup.giniapps.ksp

import meetup.giniapps.eklientannotaions.EKlient
import meetup.giniapps.eklientannotaions.EKlientGet

@EKlient("https://my-json-server.typicode.com/")
@EKlientGet(path = "elias-lm/GiniAppsKSPMeetup/db", functionName = "getEliasGiniDB")
interface MyAPI


suspend fun main() {
    val myApi: MyAPI by MyAPIEKlientFactory()

    myApi {
        getEliasGiniDB().objectX.param
    }
}