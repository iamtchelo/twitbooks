package io.paulocosta.twitbooks.auth

import arrow.core.Either
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class DevUserProvider(private val userService: UserService) : UserInfoProvider {

    @Value("\${twitter.access.token}")
    lateinit var accessToken: String

    @Value("\${twitter.access.token.secret}")
    lateinit var accessTokenSecret: String

    override fun getUser(twitterId: String): Either<User, LoginError> {
        return when (val user = userService.findByTwitterId(twitterId)) {
            null -> Either.left(User( null, twitterId, accessToken, accessTokenSecret ))
            else -> Either.left(user)
        }
    }
}