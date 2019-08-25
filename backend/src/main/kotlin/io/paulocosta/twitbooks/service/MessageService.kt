package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterApiProvider
import io.paulocosta.twitbooks.entity.*
import io.paulocosta.twitbooks.ratelimit.RateLimitWatcher
import io.paulocosta.twitbooks.repository.MessageRepository
import io.reactivex.Single
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.social.twitter.api.Tweet
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import javax.transaction.Transactional

private val logger = KotlinLogging.logger {}

@Service
class MessageService @Autowired constructor(
        val twitterApiProvider: TwitterApiProvider,
        val messageRepository: MessageRepository,
        val messageSyncStateService: MessageSyncStateService,
        val friendService: FriendService) {

    companion object {
        // 200 is the max pages supported
        const val TIMELINE_PAGE_SIZE = 200
        const val MINIMUM_DEPTH_ALLOWED_ID = 0L
    }

    fun getUnprocessedMessages(friendId: Long, pageable: Pageable): Page<Message> {
        return messageRepository.getUnprocessedMessages(friendId, pageable).map {
            Message(it.id, it.text, null, null, null)
        }
    }

    fun getUnprocesedCount(friendId: Long): Long {
        return messageRepository.getUnprocessedCount(friendId)
    }

    fun getCount(): Single<Long> {
        return Single.just(messageRepository.count())
    }

    fun syncMessages(user: User, friend: Friend, rateLimitWatcher: RateLimitWatcher): SyncResult {
        logger.info { "Starting message sync" }

        if (rateLimitWatcher.exceeded()) {
            return SyncResult.ERROR
        }
        return when (friend.messageSyncStrategy) {
            MessageSyncStrategy.DEPTH -> depthSync(user, friend, rateLimitWatcher)
            MessageSyncStrategy.NEWEST -> newestSync(user, friend, rateLimitWatcher)
        }
    }

    fun newestSync(user: User, friend: Friend, rateLimit: RateLimitWatcher): SyncResult {
        logger.info { "Starting newest sync" }
        var startId = 0L
        var endId: Long?
        while (!rateLimit.exceeded()) {
            endId = getNewestMessageId(friend)
            val messages = if (endId == null) {
                logger.info { "No message present. Getting current timeline for user ${friend.name}" }
                getCurrentUserTimeline(user, friend).messages
            } else {
                logger.info { "Getting newest messages for user ${friend.name}. startId: $startId, endId: $endId" }
                getNewestTimelineMessages(user, friend, startId, endId).messages
            }

            rateLimit.addHit()

            if (messages.isEmpty()) {
                logger.info { "No more messages for user ${friend.name}" }
                return SyncResult.SUCCESS
            }
            startId = messages.last().id
            messageRepository.saveAll(messages)
            updateMessageSyncState(friend)
        }
        return SyncResult.ERROR
    }

    fun updateMessageSyncState(friend: Friend) {
        val friendId = friend.id ?: throw IllegalStateException("User not found")
        val newestMessageId = messageRepository.getNewestMessages(friendId, PageRequest.of(0, 1)).firstOrNull()?.id ?: return
        val oldestMessageId = messageRepository.getOldestMessages(friendId, PageRequest.of(0, 1)).firstOrNull()?.id ?: return
        messageSyncStateService.saveMessageSyncState(
                friend, newestMessageId, oldestMessageId
        )
    }

    fun getNewestMessageId(friend: Friend): Long? {
        val friendId = friend.id ?: throw IllegalStateException("User not found")
        return messageSyncStateService.getMessageSyncState(friendId)?.maxId
    }

    fun getOldestMessageId(friend: Friend): Long? {
        val friendId = friend.id ?: throw IllegalStateException("User not found")
        return messageSyncStateService.getMessageSyncState(friendId)?.minId
    }

    fun depthSync(user: User, friend: Friend, rateLimitWatcher: RateLimitWatcher): SyncResult {
        logger.info { "Starting depth sync for user ${friend.screenName}" }
        while (!rateLimitWatcher.exceeded()) {
            val oldestMessageId = getOldestMessageId(friend)
            val messages = when (oldestMessageId) {
                null -> {
                    logger.info { "There is no oldest message. Getting current timeline" }
                    getCurrentUserTimeline(user, friend).messages
                }
                else -> {
                    logger.info { "Oldest message id is $oldestMessageId. Continuing depth sync" }
                    getDepthTimelineMessages(user, friend, oldestMessageId).messages
                }
            }
            if (messages.isEmpty()) {
                friendService.updateMessageSyncMode(friend, MessageSyncStrategy.NEWEST)
                logger.info { "There are no more old messages available for this user. Updating Sync Strategy to NEWEST" }
                return SyncResult.SUCCESS
            }
            messageRepository.saveAll(messages)
            updateMessageSyncState(friend)
            rateLimitWatcher.addHit()
        }
        return SyncResult.SUCCESS
    }

    @Transactional
    fun toggleProcessed(messageId: Long) {
        messageRepository.toggleProcessed(messageId)
    }

    private fun getCurrentUserTimeline(user: User, friend: Friend): MessageResult {
        val tweets = twitterApiProvider.getTwitter(user.getTwitterCredentials()).timelineOperations().getUserTimeline(friend.screenName, TIMELINE_PAGE_SIZE)
        return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getDepthTimelineMessages(user: User, friend: Friend, messageId: Long): MessageResult {
            val tweets = twitterApiProvider.getTwitter(user.getTwitterCredentials())
                    .timelineOperations().getUserTimeline(friend.screenName, TIMELINE_PAGE_SIZE,
                            MINIMUM_DEPTH_ALLOWED_ID, messageId - 1L)
            return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getNewestTimelineMessages(user: User, friend: Friend, minId: Long, maxId: Long): MessageResult {
        val tweets = twitterApiProvider.getTwitter(user.getTwitterCredentials())
                .timelineOperations().getUserTimeline(friend.screenName, TIMELINE_PAGE_SIZE,
                        maxId, minId)
        return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun toMessage(tweet: Tweet, friend: Friend): Message {
        return Message(tweet.id, tweet.text, tweet.isRetweet, tweet.createdAt, false, friend = friend)
    }

}
