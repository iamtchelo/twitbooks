package io.paulocosta.twitbooks.nerclient

import retrofit2.http.Body
import retrofit2.http.POST

interface NERApiService {

    @POST("process")
    fun process(@Body payload: NERApiPayload): NERApiResult

}