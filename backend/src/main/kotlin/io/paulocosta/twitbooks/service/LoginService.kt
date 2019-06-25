package io.paulocosta.twitbooks.service

import com.auth0.exception.APIException
import com.auth0.exception.Auth0Exception
import io.paulocosta.twitbooks.auth.Auth0Provider
import io.paulocosta.twitbooks.auth.SecurityHelper
import io.paulocosta.twitbooks.entity.User
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LoginService @Autowired constructor(
        private val auth0Provider: Auth0Provider,
        private val userService: UserService) {

    // TODO Should cache this properly. And make it thread-safe.
    private var apiToken: String = ""

    fun login() {
        try {
            val id = SecurityHelper.getTwitterId()
            updateUserInfo(id)
            logger.info { "Login successful" }
        } catch (e: Auth0Exception) {
            logger.error { "Auth0Exception ${e.message}" }
        } catch (e: APIException) {
            logger.error { "APIException ${e.message} status code ${e.statusCode}" }
        }
    }

    fun updateUserInfo(twitterId: String) {
        val user = userService.findByTwitterId(twitterId)

        if (user == null) {
            val userData = auth0Provider.geUserData(getApiToken(), twitterId)
            val identity = userData.identities[0]
            val accessToken = identity.accessToken
            val accessTokenSecret = identity.values["access_token_secret"] as String
            userService.saveUser(User(null, twitterId, accessToken, accessTokenSecret))
        }

    }

    fun getApiToken(): String {
        if (apiToken.isEmpty()) {
            apiToken = auth0Provider.getApiToken()
        }
        return apiToken
    }

}