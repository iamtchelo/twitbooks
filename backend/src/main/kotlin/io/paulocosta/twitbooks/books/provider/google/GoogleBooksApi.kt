package io.paulocosta.twitbooks.books.provider.google

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("/volumes")
    fun search(
            @Query("q") query: String,
            @Query("langRestrict") langRestrict: String,
            @Query("maxResults") maxResults: Int): Single<GoogleBooksApiResponse>
}

