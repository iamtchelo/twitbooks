package io.paulocosta.twitbooks.goodreads

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GoodreadsSearch {

    @GET("search")
    fun search(@Query("q") query: String): Single<GoodreadsResponse>

}