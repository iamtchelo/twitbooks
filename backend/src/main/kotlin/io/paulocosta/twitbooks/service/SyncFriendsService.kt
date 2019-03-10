package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.TwitterProfile
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SyncFriendsService @Autowired constructor(
        val userService: UserService,
        val friendSyncStatusService: FriendSyncStatusService,
        val rateLimitService: RateLimitService,
        val twitterProvider: TwitterProvider
) {

    fun sync(): SyncResult {
        logger.info { "Starting to sync users" }
        val rateLimit = rateLimitService.getFriendRateLimits()
        return if (rateLimit.exceeded()) {
            logger.info { "Rate limit has been exceeded for the list friends API" }
            SyncResult.ERROR
        } else {
            val latestSync = friendSyncStatusService.getLatestFriendSyncStatus()
            return when (latestSync.status) {
                Status.ABSENT -> {
                    logger.info { "No previous user sync status present. Starting full sync" }
                    doSync(rateLimit, null)
                }
                Status.SUCCESS -> {
                    logger.info { "Previous user sync was successful. Skipping further synchronization" }
                    SyncResult.SUCCESS
                }
                Status.FAILED -> {
                    logger.info { "Previous sync failed. Attempt to synchronize with previous cursor" }
                    doSync(rateLimit, latestSync.cursorId)
                }
            }
        }
    }

    private fun doSync(rateLimit: RateLimit, cursor: Long?): SyncResult {
        var hits = 0
        var lastCursor = cursor
        while (hits < rateLimit.remainingHits) {
            val profiles = if (hits == 0 && cursor == null) {
                twitterProvider.getTwitter().friendOperations().friends
            } else {
                twitterProvider.getTwitter().friendOperations().getFriendsInCursor(lastCursor!!)
            }

            hits++

            userService.saveFriends(profiles.map { toFriend(it) })
            lastCursor = profiles.nextCursor

            if (!profiles.hasNext()) {
                friendSyncStatusService.createSuccessEvent()
                return SyncResult.SUCCESS
            }
        }
        friendSyncStatusService.createSyncFailedEvent(lastCursor)
        return SyncResult.ERROR
    }

    private fun toFriend(profile: TwitterProfile): Friend {
        return Friend(
                profile.id,
                profile.name,
                profile.screenName,
                MessageSyncStrategy.DEPTH)
    }

}
