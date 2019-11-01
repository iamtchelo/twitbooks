package io.paulocosta.twitbooks.service

import com.auth0.exception.APIException
import com.auth0.exception.Auth0Exception
import io.paulocosta.twitbooks.auth.Auth0Provider
import io.paulocosta.twitbooks.auth.SecurityHelper
import io.paulocosta.twitbooks.entity.User
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LoginService(
        private val securityHelper: SecurityHelper,
        private val auth0Provider: Auth0Provider,
        private val userService: UserService) {

    private var apiToken: String = ""

    fun login() {
        try {
            val id = securityHelper.getTwitterId()
            updateUserInfo(id)
            logger.info { "Login successful" }
        } catch (e: Auth0Exception) {
            logger.error { "Auth0Exception ${e.message}" }
        } catch (e: APIException) {
            handleAuthAPIError(e)
        }
    }

    fun handleAuthAPIError(e: APIException) {
        if (e.statusCode == 401) {
            logger.info { "API Token has expired, requesting a new one" }
            this.apiToken = ""
            login()
        } else {
            // TODO proper error handling
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
