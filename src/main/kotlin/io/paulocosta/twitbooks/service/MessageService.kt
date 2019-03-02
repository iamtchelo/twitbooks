package io.paulocosta.twitbooks.service

import arrow.core.Either
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
        val messageRepository: MessageRepository,
        val userService: UserService) {

    companion object {
        // Maximum number supported by the API
        const val PAGE_SIZE = 200
        const val MINIMUM_DEPTH_ALLOWED_ID = 0L
    }

    fun syncMessages(friend: Friend) {
        logger.info { "Starting message sync" }
        when (friend.messageSyncStrategy) {
            MessageSyncStrategy.DEPTH -> depthSync(friend)
            MessageSyncStrategy.NEWEST -> newestSync(friend)
        }
    }

    private fun newestSync(friend: Friend, lowestId: Long? = null, newestMessage: Message? = null): MessageSyncResult {
        when (lowestId) {
            null -> {
                logger.info { "Getting current user timeline to see if there's any new messages" }
                val msg = messageRepository.getNewestMessages(friend.id, PageRequest.of(0, 1)).first()
                val result = getCurrentUserTimeline(friend)
                when (result) {
                    is Either.Left -> {
                        val messages = result.a.messages
                        return if (messages.isEmpty()) {
                            logger.info { "User has no messages on timeline" }
                            MessageSyncResult.RESULT_OK
                        } else {
                            messageRepository.saveAll(messages)
                            if (messages.size == PAGE_SIZE) {
                                newestSync(friend, messages.last().id, msg)
                            } else {
                                MessageSyncResult.RESULT_OK
                            }
                        }
                    }
                    is Either.Right -> {
                        logger.info { "Could not get further messages due to being rate limited." }
                        return MessageSyncResult.ERROR
                    }
                }
            }
            else -> {
                logger.info { "Timeline contains more than 200 new requests" }
                if (lowestId < newestMessage!!.id) {
                    logger.info { "Already synchronized" }
                    return MessageSyncResult.RESULT_OK
                } else {
                    val result = getNewestTimelineMessages(friend, lowestId, newestMessage.id)
                    when (result) {
                        is Either.Left -> {
                            val messages = result.a.messages
                            return if (messages.isEmpty()) {
                                logger.info { "User has no messages on timeline" }
                                MessageSyncResult.RESULT_OK
                            } else {
                                messageRepository.saveAll(messages)
                                if (messages.size == PAGE_SIZE) {
                                    newestSync(friend, messages.last().id, newestMessage)
                                } else {
                                    MessageSyncResult.RESULT_OK
                                }
                            }
                        }
                        is Either.Right -> {
                            return MessageSyncResult.ERROR
                        }
                    }
                }
            }
        }
    }

    fun depthSync(friend: Friend): MessageSyncResult {
        val rateLimit = getRateLimit()
        if (rateLimit.exceeded()) {
            return MessageSyncResult.ERROR
        } else {
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
        }
    }

    private fun getCurrentUserTimeline(friend: Friend): Either<MessageResult, RateLimited> {
        return if (isRateLimited()) {
            Either.right(RateLimited())
        } else {
            val tweets = twitterProvider.getTwitter().timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE)
            Either.left(MessageResult(tweets.map { toMessage(it, friend) }))
        }
    }

    private fun getDepthTimelineMessages(friend: Friend, message: Message): MessageResult {
            val messageId = message.id
            val tweets = twitterProvider.getTwitter()
                    .timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE,
                            MINIMUM_DEPTH_ALLOWED_ID, messageId - 1L)
            return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getNewestTimelineMessages(friend: Friend, minId: Long, maxId: Long): Either<MessageResult, RateLimited> {
        getCurrentUserTimeline(friend)
        return if (isRateLimited()) {
            Either.right(RateLimited())
        } else {
            val tweets = twitterProvider.getTwitter()
                    .timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE,
                            maxId, minId)
            Either.left(MessageResult(tweets.map { toMessage(it, friend) }))
        }
    }

    private fun isRateLimited(): Boolean = rateLimitService.getTimelineRateLimits().exceeded()

    private fun getRateLimit(): RateLimit = rateLimitService.getTimelineRateLimits()

    fun toMessage(tweet: Tweet, friend: Friend): Message {
        return Message(tweet.id, tweet.text, tweet.isRetweet, tweet.createdAt, friend)
    }

}