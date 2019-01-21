package io.paulocosta.twitbooks.api

import io.paulocosta.twitbooks.auth.TwitterAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.RateLimitStatus
import org.springframework.social.twitter.api.ResourceFamily
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Provides information on the API limits the application
 * has available. Current endpoints used:
 *
 * - statuses/user_timeline
 *
 *
 * **/

@RestController("/limits")
class RateLimitApi {

    @Autowired
    lateinit var twitterAuth: TwitterAuth

    @GetMapping
    fun getLimits(): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        return twitterAuth.getTwitter().userOperations().getRateLimitStatus(
                // ResourceFamily.SEARCH
                ResourceFamily.STATUSES
        )
    }

}