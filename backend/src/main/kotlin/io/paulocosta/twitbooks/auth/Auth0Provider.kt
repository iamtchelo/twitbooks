package io.paulocosta.twitbooks.auth

import com.auth0.client.mgmt.ManagementAPI
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class Auth0Provider {

    @Value("\${auth0.domain}")
    lateinit var domain: String

    fun managementApi(authToken: String): ManagementAPI {
        return ManagementAPI(domain, authToken)
    }

}