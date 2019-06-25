package io.paulocosta.twitbooks.auth

import org.springframework.security.core.context.SecurityContextHolder

object SecurityHelper {
    fun getTwitterId(): String {
        return SecurityContextHolder.getContext().authentication.principal as String
    }
}