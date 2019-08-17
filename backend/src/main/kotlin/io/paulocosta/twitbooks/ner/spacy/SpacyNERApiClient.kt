package io.paulocosta.twitbooks.ner.spacy

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SpacyNERApiClient {

    @POST("process")
    fun process(@Body NERPayload: SpacyNERPayload): Single<SpacyNERApiResponse>

    @GET("status")
    fun status(): Completable

}
