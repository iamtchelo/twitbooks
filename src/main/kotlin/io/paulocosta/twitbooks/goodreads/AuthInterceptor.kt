package io.paulocosta.twitbooks.goodreads

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url().newBuilder()
                .addQueryParameter("key", authKey)
                .build()
        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }

}