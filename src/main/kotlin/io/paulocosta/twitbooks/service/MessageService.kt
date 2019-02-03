package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.Friend
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

    fun getMessagesFromUser(friend: Friend): List<Message> {
        val oldestMessage: Message? = messageRepository.findFirstByFriendIdOrderByCreatedAt(friend.id)

        val messages = when(oldestMessage) {
            null -> {
                logger.info { "No messages present for user ${friend.screenName}. Retrieving current timeline" }
                getMessagesFromUserTimeline(friend)
            }
            else -> {
                logger.info { "No messages present for user ${friend.screenName}. Retrieving Tweets from id ${friend.twitterId}" }
                getMessagesFromUserTimeline(
                        friend,
                        oldestMessage.twitterId,
                        oldestMessage.twitterId)
            }
        }
        logger.info { "Got ${messages.count()} from the Timeline API" }
        return messages
    }

    private fun getMessagesFromUserTimeline(friend: Friend): List<Message> {
        validate()
        val tweets = twitterProvider.getTwitter().timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE)
        return tweets.map { toMessage(it, friend) }
    }

    private fun getMessagesFromUserTimeline(friend: Friend, sinceId: Long, maxId: Long): List<Message> {
        validate()
        val tweets = twitterProvider.getTwitter()
                .timelineOperations().getUserTimeline(friend.screenName, PAGE_SIZE, 0, maxId - 1)
        return tweets.map { toMessage(it, friend) }
    }

    private fun validate() {
        val rateLimit = rateLimitService.getTimelineRateLimits()
        if (rateLimit.exceeded()) {
            logger.warn { "Rate limit exceeded for timeline" }
            throw IllegalStateException("Rate limit exceeded")
        }
    }

    fun toMessage(tweet: Tweet, friend: Friend): Message {
        return Message(null, tweet.text, tweet.id, tweet.isRetweet, tweet.createdAt, friend)
    }

}