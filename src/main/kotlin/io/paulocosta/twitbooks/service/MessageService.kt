package io.paulocosta.twitbooks.service

import arrow.core.Either
import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.Friend
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.entity.MessageResult
import io.paulocosta.twitbooks.entity.MessageSyncStrategy
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

    private fun depthSync(friend: Friend): MessageSyncResult {
        val oldestMessage = messageRepository.getOldestMessages(friend.id, PageRequest.of(0, 1)).firstOrNull()
        logger.info { "starting depth sync. Current oldest message for user ${friend.screenName} is ${oldestMessage?.id}" }
        val result = when (oldestMessage) {
            null -> {
                logger.info { "No messages present from current user. Getting current timeline" }
                getCurrentUserTimeline(friend)
            }
            else -> {
                logger.info { "Getting older messages from user. From id ${oldestMessage.id}" }
                getDepthTimelineMessages(friend, oldestMessage)
            }
        }
        when (result) {
            is Either.Left -> {
                val messages = result.a.messages
                return if (messages.isEmpty()) {
                    logger.info { "No more messages! Depth sync over" }
                    userService.updateMessageSyncMode(friend, MessageSyncStrategy.NEWEST)
                    MessageSyncResult.RESULT_OK
                } else {
                    messageRepository.saveAll(result.a.messages)
                    depthSync(friend)
                }
            }
            is Either.Right -> {
                logger.info { "Could not get further messages due to being rate limited." }
                return MessageSyncResult.ERROR
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

    private fun getDepthTimelineMessages(friend: Friend, message: Message): Either<MessageResult, RateLimited> {
        return if (isRateLimited()) {
            Either.right(RateLimited())
        } else {
            val messageId = message.id
            val tweets = twitterProvider.getTwitter()
                    .timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE,
                            MINIMUM_DEPTH_ALLOWED_ID, messageId - 1L)
            Either.left(MessageResult(tweets.map { toMessage(it, friend) }))
        }
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

    fun toMessage(tweet: Tweet, friend: Friend): Message {
        return Message(tweet.id, tweet.text, tweet.isRetweet, tweet.createdAt, friend)
    }

}