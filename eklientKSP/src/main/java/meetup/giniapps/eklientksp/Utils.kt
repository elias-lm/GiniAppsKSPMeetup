package meetup.giniapps.eklientksp

import meetup.giniapps.eklientannotaions.EKlient
import meetup.giniapps.eklientannotaions.EKlientGet
import meetup.giniapps.eklientannotaions.EKlientPost
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Utils {

    fun networkGetCall(request: Request, okHttpGenerativeClient: OkHttpClient): String {
        okHttpGenerativeClient.newCall(request).execute().use { response ->
            if (response.isSuccessful)
                return (response.body?.string() ?: "")
            else
                throw Throwable("Network call failed with code ${response.code} for path ${request.url}")
        }
    }

    fun EKlient.validateURLRules(): EKlient {
        if (!url.endsWith("/"))
            throw IllegalArgumentException("url must end with /\n URL: $url")
        return this
    }

    fun EKlientGet.validatePathRules(): EKlientGet {
        return this.apply {
            path.validatePathRules()
        }
    }

    fun String.validatePathRules(): String {
        return if (startsWith("/"))
            throw IllegalArgumentException("path must not start with /\n PATH: $this")
        else this
    }

    fun EKlientPost.validatePathRules(): EKlientPost {
        return this.apply {
            path.validatePathRules()
        }
    }
}