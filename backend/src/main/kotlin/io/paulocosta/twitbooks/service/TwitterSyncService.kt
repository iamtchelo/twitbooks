package io.paulocosta.twitbooks.service

import arrow.core.Either
import io.paulocosta.twitbooks.entity.SyncResult
import io.paulocosta.twitbooks.ratelimit.RateLimitKeeper
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class TwitterSyncService @Autowired constructor(
        val userService: UserService,
        val syncFriendsService: SyncFriendsService,
        val messageService: MessageService,
        val rateLimitService: RateLimitService) {

    @Value("\${sync.enabled}")
    var syncEnabled: Boolean = false

    companion object {
        // Five hours
        const val SYNC_DELAY_MILLIS = 5L * 36L * 100000L
    }

    @Scheduled(fixedDelay = SYNC_DELAY_MILLIS)
    fun sync() {
        if (syncEnabled) {
            logger.info { "Starting Twitter Sync" }
            syncUsers()
            syncMessages()
        } else {
            logger.info { "Twitter sync diabled by config" }
        }
    }

    private fun syncMessages() {

        val rateLimit = when(val eitherLimit = rateLimitService.getTimelineRateLimits()) {
            is Either.Left -> eitherLimit.a
            else -> {
                logger.info { "Rate limit exceeded. Stopping message sync" }
                return
            }
        }

        val rateLimitKeeper = RateLimitKeeper(rateLimit)

        for (it in userService.getAllUsers()) {
            val result = messageService.syncMessages(it, rateLimitKeeper)
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

    private fun syncUsers() {
        when (syncFriendsService.sync()) {
            SyncResult.SUCCESS -> {
                logger.info { "User sync finished successfully" }
            }
            SyncResult.ERROR -> {
                logger.info { "User sync finished with error" }
            }
        }
    }

}