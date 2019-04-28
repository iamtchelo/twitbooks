package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.*
import io.paulocosta.twitbooks.ratelimit.RateLimitKeeper
import io.paulocosta.twitbooks.repository.MessageRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.social.twitter.api.Tweet
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MessageService @Autowired constructor(
        val twitterProvider: TwitterProvider,
        val messageRepository: MessageRepository,
        val messageSyncStateService: MessageSyncStateService,
        val userService: UserService) {

    @Value("\${spring.profiles.active}")
    lateinit var activeProfile: String

    companion object {
        // 200 is the max pages supported
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

    fun syncMessages(friend: Friend, rateLimitKeeper: RateLimitKeeper): SyncResult {
        logger.info { "Starting message sync" }

        if (rateLimitKeeper.exceeded()) {
            return SyncResult.ERROR
        }
        return when (friend.messageSyncStrategy) {
            MessageSyncStrategy.DEPTH -> depthSync(friend, rateLimitKeeper)
            MessageSyncStrategy.NEWEST -> newestSync(friend, rateLimitKeeper)
        }
    }

    fun newestSync(friend: Friend, rateLimit: RateLimitKeeper): SyncResult {
        logger.info { "Starting newest sync" }
        var startId = 0L
        var endId: Long?
        while (!rateLimit.exceeded()) {
            endId = getNewestMessageId(friend)
            val messages = if (endId == null) {
                logger.info { "No message present. Getting current timeline for user ${friend.name}" }
                getCurrentUserTimeline(friend).messages
            } else {
                logger.info { "Getting newest messages for user ${friend.name}. startId: $startId, endId: $endId" }
                getNewestTimelineMessages(friend, startId, endId).messages
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

        if (activeProfile != "prod") {
            return
        }

        val newestMessageId = messageRepository.getNewestMessages(friend.id, PageRequest.of(0, 1)).firstOrNull()?.id ?: return
        val oldestMessageId = messageRepository.getOldestMessages(friend.id, PageRequest.of(0, 1)).firstOrNull()?.id ?: return
        messageSyncStateService.saveMessageSyncState(
                friend, newestMessageId, oldestMessageId
        )
    }

    fun getNewestMessageId(friend: Friend): Long? {
        return if (activeProfile == "prod") {
            messageSyncStateService.getMessageSyncState(friend.id)?.maxId
        } else {
            messageRepository.getNewestMessages(friend.id, PageRequest.of(0, 1)).firstOrNull()?.id
        }
    }

    fun getOldestMessageId(friend: Friend): Long? {
        return if (activeProfile == "prod") {
            messageSyncStateService.getMessageSyncState(friend.id)?.minId
        } else {
            messageRepository.getOldestMessages(friend.id, PageRequest.of(0, 1)).firstOrNull()?.id
        }
    }

    fun depthSync(friend: Friend, rateLimitKeeper: RateLimitKeeper): SyncResult {
        logger.info { "Starting depth sync for user ${friend.screenName}" }
        while (!rateLimitKeeper.exceeded()) {
            val oldestMessageId = getOldestMessageId(friend)
            val messages = when (oldestMessageId) {
                null -> {
                    logger.info { "There is no oldest message. Getting current timeline" }
                    getCurrentUserTimeline(friend).messages
                }
                else -> {
                    logger.info { "Oldest message id is $oldestMessageId. Continuing depth sync" }
                    getDepthTimelineMessages(friend, oldestMessageId).messages
                }
            }
            if (messages.isEmpty()) {
                userService.updateMessageSyncMode(friend, MessageSyncStrategy.NEWEST)
                logger.info { "There are no more old messages available for this user. Updating Sync Strategy to NEWEST" }
                return SyncResult.SUCCESS
            }
            messageRepository.saveAll(messages)
            updateMessageSyncState(friend)
            rateLimitKeeper.addHit()
        }
        return SyncResult.SUCCESS
    }

    fun deleteMessage(message: Message) {
        messageRepository.delete(message)
    }

    fun update(message: Message) {
        messageRepository.save(message)
    }

    private fun getCurrentUserTimeline(friend: Friend): MessageResult {
        val tweets = twitterProvider.getTwitter().timelineOperations().getUserTimeline(friend.screenName, TIMELINE_PAGE_SIZE)
        return MessageResult(tweets.map { toMessage(it, friend) })
    }

    private fun getDepthTimelineMessages(friend: Friend, messageId: Long): MessageResult {
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