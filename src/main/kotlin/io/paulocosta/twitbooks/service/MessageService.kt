package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.*
import io.paulocosta.twitbooks.repository.MessageRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.social.twitter.api.Tweet
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

enum class MessageSyncResult {
    RESULT_OK, ERROR
}

@Service
class MessageService @Autowired constructor(
        val twitterProvider: TwitterProvider,
        val rateLimitService: RateLimitService,
        val messageRepository: MessageRepository) {

    companion object {
        // Maximum number supported by the API
        const val PAGE_SIZE = 200
        const val MINIMUM_DEPTH_ALLOWED_ID = 0L
    }

    fun syncMessages(friend: Friend): MessageSyncResult {
        logger.info { "Starting message sync" }
        val rateLimit = getRateLimit()
        if (rateLimit.exceeded()) {
            return MessageSyncResult.ERROR
        }
        return when (friend.messageSyncStrategy) {
            MessageSyncStrategy.DEPTH -> depthSync(friend, rateLimit)
            MessageSyncStrategy.NEWEST -> newestSync(friend, rateLimit)
        }
    }

    fun newestSync(friend: Friend, rateLimit: RateLimit): MessageSyncResult {
        var hits = 0
        var startId = 0L
        val endId = messageRepository.getNewestMessages(friend.id, PageRequest.of(0, 1)).first().id
        while (hits < rateLimit.remainingHits) {
            hits++
            val messages = if (hits == 0) {
                getCurrentUserTimeline(friend).messages
            } else {
                getNewestTimelineMessages(friend, startId, endId).messages
            }

            if (messages.isEmpty()) {
                return MessageSyncResult.RESULT_OK
            }
            startId = messages.last().id
            messageRepository.saveAll(messages)
        }
        return MessageSyncResult.ERROR
    }

    fun depthSync(friend: Friend, rateLimit: RateLimit): MessageSyncResult {
        var hits = 0
        while (hits < rateLimit.remainingHits) {
            hits++
            val oldestMessage = messageRepository.getOldestMessages(friend.id, PageRequest.of(0, 1)).firstOrNull()
            when (oldestMessage) {
                null -> {
                    logger.info { "There are no oldest messages. Should not be doing depth sync" }
                    return MessageSyncResult.ERROR
                }
                else -> {
                    val messages = getDepthTimelineMessages(friend, oldestMessage).messages
                    if (messages.isEmpty()) {
                        logger.info { "There are no more old messages available for this user" }
                        return MessageSyncResult.RESULT_OK
                    }
                    messageRepository.saveAll(messages)
                }
            }
        }
        return MessageSyncResult.ERROR
    }

    private fun getCurrentUserTimeline(friend: Friend): MessageResult {
        val tweets = twitterProvider.getTwitter().timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE)
        return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getDepthTimelineMessages(friend: Friend, message: Message): MessageResult {
            val messageId = message.id
            val tweets = twitterProvider.getTwitter()
                    .timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE,
                            MINIMUM_DEPTH_ALLOWED_ID, messageId - 1L)
            return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getNewestTimelineMessages(friend: Friend, minId: Long, maxId: Long): MessageResult {
        val tweets = twitterProvider.getTwitter()
                .timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE,
                        maxId, minId)
        return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getRateLimit(): RateLimit = rateLimitService.getTimelineRateLimits()

    fun toMessage(tweet: Tweet, friend: Friend): Message {
        return Message(tweet.id, tweet.text, tweet.isRetweet, tweet.createdAt, friend)
    }

}