package io.paulocosta.twitbooks.service

import arrow.core.Either
import io.paulocosta.twitbooks.entity.SyncResult
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.ratelimit.RateLimitWatcher
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class TwitterSyncService @Autowired constructor(
        val friendService: FriendService,
        val syncFriendsService: SyncFriendsService,
        val messageService: MessageService,
        val rateLimitService: RateLimitService) {

    fun sync(user: User) {
        logger.info { "Starting Twitter Sync" }
        syncFriends(user)
//        syncMessages()
    }

    private fun syncMessages(user: User) {

        val rateLimit = when(val eitherLimit = rateLimitService.getTimelineRateLimits(user.getTwitterCredentials())) {
            is Either.Left -> eitherLimit.a
            else -> {
                logger.info { "Rate limit exceeded. Stopping message sync" }
                return
            }
        }

        val rateLimitWatcher = RateLimitWatcher(rateLimit)

        for (it in friendService.getAllFriends()) {
            val result = messageService.syncMessages(user, it, rateLimitWatcher)
            when (result) {
                SyncResult.ERROR -> {
                    logger.info { "Stopping message sync due to rate limits" }
                    return
                }
                else -> {
                    logger.info { "sync for user ${it.name} finished successfully" }
                }
            }
        }
        logger.info { "Message sync finished!" }
    }

    private fun syncFriends(user: User) {
        when (syncFriendsService.sync(user)) {
            SyncResult.SUCCESS -> {
                logger.info { "User sync finished successfully" }
            }
            SyncResult.ERROR -> {
                logger.info { "User sync finished with error" }
            }
        }
    }

}