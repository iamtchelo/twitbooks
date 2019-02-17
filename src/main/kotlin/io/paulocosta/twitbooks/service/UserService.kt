package io.paulocosta.twitbooks.service

import arrow.core.Either
import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.Friend
import io.paulocosta.twitbooks.entity.MessageSyncStrategy
import io.paulocosta.twitbooks.repository.FriendsRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.CursoredList
import org.springframework.social.twitter.api.TwitterProfile
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {}

sealed class FriendRequestStatus

data class RateLimited(var cursorId: Long? = null) : FriendRequestStatus()
object CursorEnd: FriendRequestStatus()
data class HasMore(val cursorId: Long): FriendRequestStatus()


@Service
class UserService @Autowired constructor(
        private val rateLimitService: RateLimitService,
        private val twitterProvider: TwitterProvider,
        private val friendsRepository: FriendsRepository) {

    fun fullSync(): FriendRequestStatus {
        return iterateCursor()
    }

    fun cursorSync(cursorId: Long?): FriendRequestStatus {
        return iterateCursor(cursorId)
    }

    fun getAllUsers(): List<Friend> = friendsRepository.findAll()

    private fun iterateCursor(cursorId: Long? = null): FriendRequestStatus {
        val response = handleFriendsResponse(getNextFriendRequest(cursorId))
        return when (response) {
            is RateLimited -> RateLimited(response.cursorId)
            is HasMore -> iterateCursor(response.cursorId)
            is CursorEnd -> CursorEnd
        }
    }

    private fun getNextFriendRequest(cursorId: Long?): Either<CursoredList<TwitterProfile>, RateLimited> {
        val rateLimit = rateLimitService.getFriendRateLimits()
        return when (rateLimit.exceeded()) {
            true -> {
                logger.info { "Rate limit exceeded on friends API" }
                Either.right(RateLimited(cursorId))
            }
            false -> {
                return when (cursorId) {
                    null -> Either.left(twitterProvider.getTwitter().friendOperations().friends)
                    else -> Either.left(twitterProvider.getTwitter().friendOperations().getFriendsInCursor(cursorId))
                }
            }
        }
    }

    private fun handleFriendsResponse(response: Either<CursoredList<TwitterProfile>, RateLimited>): FriendRequestStatus {
        return when (response) {
            is Either.Left -> {
                val friendsResponse = response.a
                saveFriends(friendsResponse)
                if (friendsResponse.hasNext()) {
                    HasMore(friendsResponse.nextCursor)
                } else {
                    CursorEnd
                }
            }
            is Either.Right -> { response.b }
        }
    }

    private fun saveFriends(profiles: List<TwitterProfile>) {
        val friends = profiles.map { toFriend(it) }
        friendsRepository.saveAll(friends)
    }

    private fun toFriend(profile: TwitterProfile): Friend {
        return Friend(
                null,
                profile.id,
                profile.name,
                profile.screenName,
                MessageSyncStrategy.DEPTH)
    }

    fun updateMessageSyncMode(friend: Friend, messageSyncStrategy: MessageSyncStrategy) {
        friendsRepository.save(Friend(
                friend.id, friend.twitterId, friend.name, friend.screenName, messageSyncStrategy
        ))
    }

    fun findFriendById(id: Long): Optional<Friend> {
        return friendsRepository.findById(id)
    }

}