package io.paulocosta.twitbooks.service

import arrow.core.Either
import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.*
import io.paulocosta.twitbooks.repository.MessageRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.social.twitter.api.Tweet
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MessageService @Autowired constructor(
        val twitterProvider: TwitterProvider,
        val rateLimitService: RateLimitService,
        val messageRepository: MessageRepository) {

    companion object {
        // Maximum number supported by the API
        const val TIMELINE_PAGE_SIZE = 200
        const val MINIMUM_DEPTH_ALLOWED_ID = 0L
    }

    fun getAllMessages(friendId: Long, pageable: Pageable): Page<Message> {
        return messageRepository.getAllByFriendIdOrderByIdAsc(friendId, pageable)
    }

    fun getUnprocessedMessages(friendId: Long, pageable: Pageable): Page<Message> {
        return messageRepository.getUnprocessedMessages(friendId, pageable)
    }

    fun getUnprocessedMessageCount(friendId: Long): Int {
        return messageRepository.getUnprocessedMessageCount(friendId)
    }

    fun syncMessages(friend: Friend): SyncResult {
        logger.info { "Starting message sync" }
        val rateLimit = when(val eitherLimit = rateLimitService.getTimelineRateLimits()) {
            is Either.Left -> eitherLimit.a
            is Either.Right -> return SyncResult.ERROR
        }

        if (rateLimit.exceeded()) {
            return SyncResult.ERROR
        }
        return when (friend.messageSyncStrategy) {
            MessageSyncStrategy.DEPTH -> depthSync(friend, rateLimit)
            MessageSyncStrategy.NEWEST -> newestSync(friend, rateLimit)
        }
    }

    fun newestSync(friend: Friend, rateLimit: RateLimit): SyncResult {
        logger.info { "Starting newest sync" }
        var hits = 0
        var startId = 0L
        val endId = messageRepository.getNewestMessages(friend.id, PageRequest.of(0, 1)).first().id
        while (hits < rateLimit.remainingHits) {
            val messages = if (hits == 0) {
                getCurrentUserTimeline(friend).messages
            } else {
                getNewestTimelineMessages(friend, startId, endId).messages
            }

            hits++

            if (messages.isEmpty()) {
                return SyncResult.SUCCESS
            }
            startId = messages.last().id
            messageRepository.saveAll(messages)
        }
        return SyncResult.ERROR
    }

    fun depthSync(friend: Friend, rateLimit: RateLimit): SyncResult {
        logger.info { "Starting depth sync for user ${friend.screenName}" }
        var hits = 0
        while (hits < rateLimit.remainingHits) {
            val oldestMessage = messageRepository.getOldestMessages(friend.id, PageRequest.of(0, 1)).firstOrNull()
            val messages = when (oldestMessage) {
                null -> {
                    logger.info { "There is no oldest message. Getting current timeline" }
                    getCurrentUserTimeline(friend).messages
                }
                else -> {
                    getDepthTimelineMessages(friend, oldestMessage).messages
                }
            }
            if (messages.isEmpty()) {
                logger.info { "There are no more old messages available for this user" }
                return SyncResult.SUCCESS
            }
            messageRepository.saveAll(messages)
            hits++
        }
        return SyncResult.SUCCESS
    }

    fun update(message: Message) {
        messageRepository.save(message)
    }

    private fun getCurrentUserTimeline(friend: Friend): MessageResult {
        val tweets = twitterProvider.getTwitter().timelineOperations().getUserTimeline(friend.screenName, TIMELINE_PAGE_SIZE)
        return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getDepthTimelineMessages(friend: Friend, message: Message): MessageResult {
            val messageId = message.id
            val tweets = twitterProvider.getTwitter()
                    .timelineOperations().getUserTimeline(friend.screenName, TIMELINE_PAGE_SIZE,
                            MINIMUM_DEPTH_ALLOWED_ID, messageId - 1L)
            return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getNewestTimelineMessages(friend: Friend, minId: Long, maxId: Long): MessageResult {
        val tweets = twitterProvider.getTwitter()
                .timelineOperations().getUserTimeline(friend.screenName, TIMELINE_PAGE_SIZE,
                        maxId, minId)
        return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun toMessage(tweet: Tweet, friend: Friend): Message {
        return Message(tweet.id, tweet.text, tweet.isRetweet, tweet.createdAt, friend)
    }

}