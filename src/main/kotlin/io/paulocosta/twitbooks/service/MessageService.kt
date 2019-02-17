package io.paulocosta.twitbooks.service

import arrow.core.Either
import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.Friend
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.entity.MessageResult
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

    fun syncMessages(friend: Friend) {
        logger.info { "Starting message sync" }
        depthSync(friend)
    }

    private fun depthSync(friend: Friend): MessageSyncResult {
        val oldestMessage = messageRepository.getMessages(friend.id!!, PageRequest.of(0, 1)).firstOrNull()
        logger.info { "starting depth sync. Current oldest message for user ${friend.screenName} is ${oldestMessage?.twitterId}" }
        val result = when (oldestMessage) {
            null -> {
                logger.info { "No messages present from current user. Getting current timeline" }
                getCurrentUserTimeline(friend)
            }
            else -> {
                logger.info { "Getting older messages from user. From id ${oldestMessage.twitterId}" }
                getMessagesFromUserTimeline(friend, oldestMessage)
            }
        }
        when (result) {
            is Either.Left -> {
                val messages = result.a.messages
                return if (messages.isEmpty()) {
                    logger.info { "" }
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

    private fun getMessagesFromUserTimeline(friend: Friend, message: Message): Either<MessageResult, RateLimited> {
        return if (isRateLimited()) {
            Either.right(RateLimited())
        } else {
            val messageId = message.twitterId
            val tweets = twitterProvider.getTwitter()
                    .timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE,
                            MINIMUM_DEPTH_ALLOWED_ID, messageId - 1L)
            Either.left(MessageResult(tweets.map { toMessage(it, friend) }))
        }
    }

    private fun isRateLimited(): Boolean = rateLimitService.getTimelineRateLimits().exceeded()

    fun toMessage(tweet: Tweet, friend: Friend): Message {
        return Message(null, tweet.text, tweet.id, tweet.isRetweet, tweet.createdAt, friend)
    }

}