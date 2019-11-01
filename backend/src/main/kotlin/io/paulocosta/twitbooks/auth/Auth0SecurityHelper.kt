package io.paulocosta.twitbooks.auth

import org.springframework.context.annotation.Profile
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
@Profile("prod")
class Auth0SecurityHelper : SecurityHelper {
    override fun getTwitterId(): String {
        return SecurityContextHolder.getContext().authentication.principal as String
    }
}
