package io.paulocosta.twitbooks.service

import com.auth0.exception.APIException
import com.auth0.exception.Auth0Exception
import io.paulocosta.twitbooks.auth.Auth0Provider
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LoginService @Autowired constructor(private val auth0Provider: Auth0Provider) {

    // TODO Should cache this properly
    private var apiToken: String = ""

    fun login() {
        val id = SecurityContextHolder.getContext().authentication.principal as String
        // Should see how to cache this.
        try {
            val userData = auth0Provider.geUserData(getApiToken(), id)
            // Twitter's access token
            val accessToken = userData.identities[0].accessToken
            logger.info { "Login successful" }
        } catch (e: Auth0Exception) {
            logger.error { e.message }
        } catch (e: APIException) {
            logger.error { e.message }
        }
    }

    fun getApiToken(): String {
        if (apiToken.isEmpty()) {
            apiToken = auth0Provider.getApiToken()
        }
        return apiToken
    }

}