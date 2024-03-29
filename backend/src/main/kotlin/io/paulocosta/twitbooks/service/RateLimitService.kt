package io.paulocosta.twitbooks.service

import arrow.core.Either
import io.paulocosta.twitbooks.auth.TwitterApiProvider
import io.paulocosta.twitbooks.entity.RateLimit
import io.paulocosta.twitbooks.entity.TwitterApiCredentials
import io.paulocosta.twitbooks.error.TwitterApiError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.ApiException
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
    lateinit var twitterApiProvider: TwitterApiProvider

    fun getTimelineRateLimits(twitterApiCredentials: TwitterApiCredentials): Either<RateLimit, TwitterApiError> {
        return getRateLimit(twitterApiCredentials, ResourceFamily.STATUSES, USER_TIMELINE_ENDPOINT)
    }

    fun getFriendRateLimits(twitterApiCredentials: TwitterApiCredentials): Either<RateLimit, TwitterApiError> {
        return getRateLimit(twitterApiCredentials, ResourceFamily.FRIENDS, FRIENDS_ENDPOINT)
    }

    fun getRateLimits(twitterApiCredentials: TwitterApiCredentials): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        return twitterApiProvider.getTwitter(twitterApiCredentials)
                .userOperations()
                .getRateLimitStatus(ResourceFamily.FRIENDS, ResourceFamily.STATUSES)
    }

    private fun getRateLimit(twitterApiCredentials: TwitterApiCredentials, resourceFamily: ResourceFamily, endpoint: String): Either<RateLimit, TwitterApiError> {
        val response = try {
            twitterApiProvider.getTwitter(twitterApiCredentials)
                    .userOperations()
                    .getRateLimitStatus(resourceFamily)[resourceFamily]
        } catch (e: ApiException) {
            return Either.right(TwitterApiError())
        }

        val rateLimitStatus = response?.filter { it.endpoint ==  endpoint } ?: emptyList()
        if (rateLimitStatus.isEmpty()) {
            throw IllegalStateException("Could not obtain rate limit")
        }
        return Either.left(toRateLimit(rateLimitStatus[0]))
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