package io.paulocosta.twitbooks.controller.debug

import io.paulocosta.twitbooks.service.RateLimitService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.social.twitter.api.RateLimitStatus
import org.springframework.social.twitter.api.ResourceFamily
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


/**
 * Provides information on the API limits the application
 * has available. Current endpoints used:
 *
 * - statuses/user_timeline
 * **/

@RestController("/debug/limits")
@Profile("dev")
class RateLimitController @Autowired constructor(val rateLimitService: RateLimitService) {

    @GetMapping
    fun getLimits(): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        return null
//        return rateLimitService.getRateLimits()
    }

}