package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.repository.MessageRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.Tweet
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

private val logger = KotlinLogging.logger {}

@Service
class MessageService @Autowired constructor(
        val twitterProvider: TwitterProvider,
        val rateLimitService: RateLimitService,
        val messageRepository: MessageRepository) {

    // should be a configuration eventually
    companion object {
        const val PAGE_SIZE = 10
    }

    fun getMessagesFromUser(userTwitterId: Long, screenName: String): List<Message> {
        val oldestMessage: Message? = messageRepository.findOldestMessage(userTwitterId)

        val messages = when(oldestMessage) {
            null -> {
                logger.info { "No messages present for user $screenName. Retrieving current timeline" }
                getMessagesFromUserTimeline(screenName)
            }
            else -> {
                logger.info { "No messages present for user $screenName. Retrieving Tweets from id $userTwitterId" }
                getMessagesFromUserTimeline(
                        screenName,
                        oldestMessage.twitterId,
                        oldestMessage.twitterId)
            }
        }
        logger.info { "Got ${messages.count()} from the Timeline API" }
        return messages
    }

    private fun getMessagesFromUserTimeline(screenName: String): List<Message> {
        validate()
        val tweets = twitterProvider.getTwitter().timelineOperations().getUserTimeline(screenName, PAGE_SIZE)
        return tweets.map { toMessage(it) }
    }

    private fun getMessagesFromUserTimeline(screenName: String, sinceId: Long, maxId: Long): List<Message> {
        validate()
        val tweets = twitterProvider.getTwitter()
                .timelineOperations().getUserTimeline(screenName, PAGE_SIZE, sinceId, maxId)
        return tweets.map { toMessage(it) }
    }

    private fun validate() {
        val rateLimit = rateLimitService.getTimelineRateLimits()
        if (rateLimit.exceeded()) {
            logger.warn { "Rate limit exceeded for timeline" }
            throw IllegalStateException("Rate limit exceeded")
        }
    }

    fun toMessage(tweet: Tweet): Message {
        return Message(null, tweet.text, tweet.id, tweet.isRetweet, tweet.createdAt)
    }

}