package io.paulocosta.twitbooks.auth

import com.auth0.exception.Auth0Exception

sealed class LoginError
object TokenExpired : LoginError()
data class Auth0Error(val e: Throwable) : LoginError()
