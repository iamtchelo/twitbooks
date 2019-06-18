package io.paulocosta.twitbooks.controller.v1

import io.paulocosta.twitbooks.service.LoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/login")
class LoginController @Autowired constructor(val loginService: LoginService) {

    @GetMapping
    fun login() {
        loginService.login()
    }

}