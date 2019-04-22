package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.entity.RateLimit
import io.paulocosta.twitbooks.service.RateLimitService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


/**
 * Provides information on the API limits the application
 * has available. Current endpoints used:
 *
 * - statuses/user_timeline
 * **/

@RestController("/limits")
class RateLimitController @Autowired constructor(val rateLimitService: RateLimitService) {

    @GetMapping
    fun getLimits(): List<RateLimit> {
        return emptyList()
    }

}