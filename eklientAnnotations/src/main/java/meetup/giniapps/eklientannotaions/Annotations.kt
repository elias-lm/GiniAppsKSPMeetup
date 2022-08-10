package meetup.giniapps.eklientannotaions

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class EKlient(val url: String = "")

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class EKlientGet(
    val path: String = "",
    val functionName: String = ""
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.TYPE)
@Repeatable
annotation class EKlientPost(
    val path: String = "",
    val functionName: String = ""
)
