package io.paulocosta.twitbooks.controller.debug

import com.auth0.client.mgmt.ClientGrantsEntity
import io.paulocosta.twitbooks.auth.Auth0Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Profile("dev")
@RestController
@RequestMapping("/debug/auth")
class Auth0Controller @Autowired constructor(val provider: Auth0Provider) {

    @GetMapping
    fun getClientGrants(): ClientGrantsEntity? {
        // TODO extract the token from the security context
        return provider.managementApi("").clientGrants()
    }

}