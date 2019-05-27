package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.Auth0Provider
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LoginService @Autowired constructor(val auth0Provider: Auth0Provider) {

    fun login() {
        val loginData = SecurityContextHolder.getContext().authentication
        logger.info { "login" }
    }

}