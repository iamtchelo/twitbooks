package io.paulocosta.twitbooks.nerclient

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class NERClientConfig {

    @Value("\${ner.client.url}")
    lateinit var clientUrl: String

    @Bean
    fun provideNERRetrofitClient(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(clientUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Bean
    fun provideNERApi(): NERApiService {
        return provideNERRetrofitClient().create(NERApiService::class.java)
    }

}
