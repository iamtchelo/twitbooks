package io.paulocosta.twitbooks.auth

import com.auth0.client.auth.AuthAPI
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.client.mgmt.filter.UserFilter
import io.paulocosta.twitbooks.entity.TwitterUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod")
class Auth0Provider {

    @Value("\${auth0.audience}")
    lateinit var audience: String

    @Value("\${auth0.api.client.id}")
    lateinit var clientId: String

    @Value("\${auth0.api.client.secret}")
    lateinit var clientSecret: String

    fun geUserData(apiToken: String, userId: String): TwitterUser {
        return ManagementAPI(audience, apiToken).users().get(userId, UserFilter()).execute()
    }

    fun getApiToken(): String {
        val authAPI = AuthAPI(audience, clientId, clientSecret)
        val authRequest = authAPI.requestToken("${audience}api/v2/")
        val tokenHolder = authRequest.execute()
        return tokenHolder.accessToken
    }

}
