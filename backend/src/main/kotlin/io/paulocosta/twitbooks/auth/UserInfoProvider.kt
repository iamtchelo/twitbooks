package io.paulocosta.twitbooks.auth

import arrow.core.Either
import io.paulocosta.twitbooks.entity.User

interface UserInfoProvider {
    fun getUser(twitterId: String): Either<User, LoginError>
}
