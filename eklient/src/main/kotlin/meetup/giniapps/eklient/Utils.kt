package meetup.giniapps.eklient

import okhttp3.OkHttpClient


import okhttp3.Request
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Utils {

    suspend fun <T> OkHttpClient.call(url: String, deserializer: (String) -> T) =
        suspendCoroutine<T> {
            newCall(Request.Builder().url(url).build()).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    it.resume(deserializer(body ?: ""))
                } else {
                    it.resumeWithException(Exception("Error ${response.code}"))
                }
            }
        }

}
