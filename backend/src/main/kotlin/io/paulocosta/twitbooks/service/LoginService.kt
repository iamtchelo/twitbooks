package io.paulocosta.twitbooks.service

import com.auth0.exception.APIException
import com.auth0.exception.Auth0Exception
import io.paulocosta.twitbooks.auth.Auth0Provider
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
            val id = SecurityContextHolder.getContext().authentication.principal as String
            updateUserInfo(id)
            logger.info { "Login successful" }
        } catch (e: Auth0Exception) {
            logger.error { e.message }
        } catch (e: APIException) {
            logger.error { e.message }
        }
    }

    fun updateUserInfo(userId: String) {
        val user = userService.getUser(userId)

        if (user?.accessToken == null) {
            val userData = auth0Provider.geUserData(getApiToken(), userId)
            val identity = userData.identities[0]
            val accessToken = identity.accessToken
            val accessTokenSecret = identity.values["access_token_secret"]
            userService.saveUser(User(userId, accessToken))
        }

    }

    fun getApiToken(): String {
        if (apiToken.isEmpty()) {
            apiToken = auth0Provider.getApiToken()
        }
        return apiToken
    }

}