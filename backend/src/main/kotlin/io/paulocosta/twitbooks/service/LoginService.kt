package io.paulocosta.twitbooks.service

import arrow.core.Either
import io.paulocosta.twitbooks.auth.LoginError
import io.paulocosta.twitbooks.auth.SecurityHelper
import io.paulocosta.twitbooks.auth.TokenExpired
import io.paulocosta.twitbooks.auth.UserInfoProvider
import io.paulocosta.twitbooks.entity.User
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class LoginService(
        private val securityHelper: SecurityHelper,
        private val userInfoProvider: UserInfoProvider,
        private val userService: UserService) {

    fun login() {
        val id = securityHelper.getTwitterId()
        when (val userInfo = userInfoProvider.getUser(id)) {
            is Either.Left -> updateUserInfo(userInfo.a)
            is Either.Right -> handleLoginError(userInfo.b)
        }
    }

    fun handleLoginError(loginError: LoginError) {
        when (loginError) {
            is TokenExpired -> { login() }
            else -> { throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR) }
        }
    }

    fun updateUserInfo(user: User) = userService.saveUser(user)

}
