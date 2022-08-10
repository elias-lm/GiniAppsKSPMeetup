package meetup.giniapps.eklientksp.eklient

import meetup.giniapps.eklientannotaions.EKlientGet
import meetup.giniapps.eklientannotaions.EKlientPost

sealed class EKlientMethods(val path: String) {
    class GET(path: String) : EKlientMethods(path)
    class POST(path: String) : EKlientMethods(path)
}

fun Annotation.toEKlientMethod() : EKlientMethods {
    return when (this) {
        is EKlientGet -> EKlientMethods.GET(path)
        is EKlientPost -> EKlientMethods.POST(path)
        else -> throw IllegalArgumentException("Unknown annotation")
    }
}

fun EKlientMethods.toEKlientAnnotation() : Annotation {
    return when (this) {
        is EKlientMethods.GET -> EKlientGet(path)
        is EKlientMethods.POST -> EKlientPost(path)
    }
}