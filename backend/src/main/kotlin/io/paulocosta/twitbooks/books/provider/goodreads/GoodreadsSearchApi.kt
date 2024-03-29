package io.paulocosta.twitbooks.books.provider.goodreads

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GoodreadsSearchApi     {

    @GET("search")
    fun search(@Query("q") query: String): Single<GoodreadsResponse>

}
