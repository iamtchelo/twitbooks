package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.RateLimitStatus
import org.springframework.social.twitter.api.ResourceFamily
import org.springframework.stereotype.Service

@Service
class RateLimitService {

    @Autowired
    lateinit var twitterAuth: TwitterAuth

    fun getTimelineRateLimits(): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        return getRateLimit(ResourceFamily.STATUSES)
    }

    private fun getRateLimit(resourceFamily: ResourceFamily): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        return twitterAuth.getTwitter()
                .userOperations()
                .getRateLimitStatus(resourceFamily)
    }

}