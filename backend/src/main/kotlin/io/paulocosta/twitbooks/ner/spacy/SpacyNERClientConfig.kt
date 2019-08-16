package io.paulocosta.twitbooks.ner.spacy

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class SpacyNERClientConfig {

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
    fun provideNERApi(): SpacyNERApiClient {
        return provideNERRetrofitClient().create(SpacyNERApiClient::class.java)
    }

}
