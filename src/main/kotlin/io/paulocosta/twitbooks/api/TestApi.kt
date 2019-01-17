package io.paulocosta.twitbooks.api

import io.paulocosta.twitbooks.auth.TwitterAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/test")
class TestApi {

    @Autowired
    lateinit var twitterAuth: TwitterAuth

    @GetMapping
    fun testAuthenticity(): String {
        val template = twitterAuth.getTemplate()
        return "authenticated: ${template.isAuthorized}"
    }

}