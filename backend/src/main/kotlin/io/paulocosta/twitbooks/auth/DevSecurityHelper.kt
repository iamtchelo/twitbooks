package io.paulocosta.twitbooks.auth

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class DevSecurityHelper : SecurityHelper {
    override fun getTwitterId(): String {
        return "123456"
    }
}
