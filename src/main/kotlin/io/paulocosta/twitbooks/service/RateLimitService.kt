package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.RateLimit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.RateLimitStatus
import org.springframework.social.twitter.api.ResourceFamily
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

/**
 * Provides information on the API limits the application
 * has available. Current endpoints used:
 *
 * - statuses/user_timeline
 *
 *
 * **/
@Service
class RateLimitService {

    companion object {
        const val USER_TIMELINE_ENDPOINT = "/statuses/user_timeline"
    }

    @Autowired
    lateinit var twitterProvider: TwitterProvider

    fun getTimelineRateLimits(): RateLimit {
        return parseResponse(getRateLimit(ResourceFamily.STATUSES))
    }

    fun getFriendRateLimits(): RateLimit {
        return parseResponse(getRateLimit(ResourceFamily.FRIENDS))
    }

    private fun parseResponse(result: MutableMap<ResourceFamily, MutableList<RateLimitStatus>>?): RateLimit {
        val statuses: MutableList<RateLimitStatus>? = result?.get(ResourceFamily.STATUSES)
        val rateLimitStatus = statuses?.filter { it.endpoint ==  USER_TIMELINE_ENDPOINT } ?: emptyList()
        if (rateLimitStatus.isEmpty()) {
            throw IllegalStateException("Could not obtain rate limit")
        }
        return toRateLimit(rateLimitStatus[0])
    }

    private fun getRateLimit(resourceFamily: ResourceFamily): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        return twitterProvider.getTwitter()
                .userOperations()
                .getRateLimitStatus(resourceFamily)
    }


    private fun toRateLimit(limit: RateLimitStatus): RateLimit {
        return RateLimit(
                limit.quarterOfHourLimit,
                limit.remainingHits,
                limit.resetTimeInSeconds,
                limit.resetTime
        )
    }

}