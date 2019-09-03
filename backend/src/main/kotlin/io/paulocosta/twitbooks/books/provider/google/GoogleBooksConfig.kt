package io.paulocosta.twitbooks.books.provider.google

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Configuration
@Profile("google")
class GoogleBooksConfig {

    fun retrofitClient(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Bean
    fun googleBooksSearchApi(): GoogleBooksApi {
        return retrofitClient()
                .create(GoogleBooksApi::class.java)
    }

}
