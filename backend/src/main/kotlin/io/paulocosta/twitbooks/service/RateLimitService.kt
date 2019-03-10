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
 * - statuses/friends
 *
 * **/
@Service
class RateLimitService {

    companion object {
        const val USER_TIMELINE_ENDPOINT = "/statuses/user_timeline"
        const val FRIENDS_ENDPOINT = "/friends/list"
    }

    @Autowired
    lateinit var twitterProvider: TwitterProvider

    fun getTimelineRateLimits(): RateLimit {
        return getRateLimit(ResourceFamily.STATUSES, USER_TIMELINE_ENDPOINT)
    }

    fun getFriendRateLimits(): RateLimit {
        return getRateLimit(ResourceFamily.FRIENDS, FRIENDS_ENDPOINT)
    }

    private fun getRateLimit(
            resourceFamily: ResourceFamily, endpoint: String): RateLimit {

        val response = twitterProvider.getTwitter()
                .userOperations()
                .getRateLimitStatus(resourceFamily)

        val statuses: MutableList<RateLimitStatus>? = response?.get(resourceFamily)
        val rateLimitStatus = statuses?.filter { it.endpoint ==  endpoint } ?: emptyList()
        if (rateLimitStatus.isEmpty()) {
            throw IllegalStateException("Could not obtain rate limit")
        }
        return toRateLimit(rateLimitStatus[0])
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