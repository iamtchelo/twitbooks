package io.paulocosta.twitbooks.books.provider.goodreads

import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Configuration
@Profile("goodreads")
class GoodreadsConfig {

    @Value("\${goodreads.key}")
    lateinit var apiKey: String

    fun retrofitClient(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://www.goodreads.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient())
                .build()
    }

    fun httpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(apiKey))
                .build()
    }

    @Bean
    fun searchApi(): GoodreadsSearch {
        return retrofitClient().create(GoodreadsSearch::class.java)
    }

}
