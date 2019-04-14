package io.paulocosta.twitbooks.goodreads

import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Configuration
class GoodreadsConfig {

    @Value("\${goodreads.key}")
    lateinit var apiKey: String

    @Bean
    fun retrofitClient(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://www.goodreads.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
    }

    @Bean
    fun httpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(apiKey))
                .build()
    }

    @Bean
    fun searchApi(): GoodreadsSearch {
        return retrofitClient(httpClient()).create(GoodreadsSearch::class.java)
    }

}