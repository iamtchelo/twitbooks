package io.paulocosta.twitbooks.auth

import arrow.core.Either
import com.auth0.exception.APIException
import com.auth0.exception.Auth0Exception
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.service.UserService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("prod")
class Auth0UserProvider(private val userService: UserService,
                        private val auth0Provider: Auth0Provider) : UserInfoProvider {
    override fun getUser(twitterId: String): Either<User, LoginError> {
        try {
            val user = userService.findByTwitterId(twitterId)
            return if (user == null) {
                val userData = auth0Provider.geUserData(twitterId)
                val identity = userData.identities[0]
                val accessToken = identity.accessToken
                val accessTokenSecret = identity.values["access_token_secret"] as String
                Either.left(User(null, twitterId, accessToken, accessTokenSecret))
            } else {
                Either.left(user)
            }
        } catch (e: Auth0Exception) {
            return Either.right(Auth0Error(e))
        } catch (e: APIException) {
            return if (e.statusCode == 401) {
                Either.right(TokenExpired)
            } else {
                Either.right(Auth0Error(e))
            }
        }
    }
}