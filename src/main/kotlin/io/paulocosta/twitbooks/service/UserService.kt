package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.Friend import io.paulocosta.twitbooks.repository.FriendsRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.CursoredList
import org.springframework.social.twitter.api.TwitterProfile
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

private val logger = KotlinLogging.logger {}

sealed class FriendRequestStatus

object RateLimited : FriendRequestStatus()
object CursorEnd: FriendRequestStatus()
data class HasMore(val cursorId: Long): FriendRequestStatus()

enum class SyncStatus {
    FAIL, SUCCESS
}

@Service
class UserService @Autowired constructor(
        val rateLimitService: RateLimitService,
        val twitterProvider: TwitterProvider,
        val friendsRepository: FriendsRepository) {

    /**
     * TODO: check how to iterate the cursor here
     **/
    fun syncUsers(): SyncStatus {
        return SyncStatus.FAIL
    }

    private fun iterateCursor(cursorId: Long, initial: Boolean): FriendRequestStatus {
        val next = if (initial) {
            twitterProvider.getTwitter().friendOperations().friends
        } else {
            twitterProvider.getTwitter().friendOperations().getFriendsInCursor(cursorId)
        }
        val response = handleFriendsResponse(next)
        return when (response) {
            is RateLimited -> throw IllegalStateException("Rate limit exceeded")
            is HasMore -> iterateCursor(response.cursorId, false)
            is CursorEnd -> CursorEnd
        }
    }

    /**
     * Get the initial list of friends. It provides a cursor for the next iteration if
     * there is one.
     **/
    private fun getInitialFriends(): FriendRequestStatus {
        val rateLimit = rateLimitService.getFriendRateLimits()
        return when (rateLimit.exceeded()) {
            true -> {
                logger.info { "Rate limit exceeded on friends API" }
                RateLimited
            }
            false ->  {
                val friends = twitterProvider.getTwitter().friendOperations().friends
                handleFriendsResponse(friends)
            }
        }
    }

    private fun handleFriendsResponse(friends: CursoredList<TwitterProfile>): FriendRequestStatus {
        saveFriends(friends)
        return if (friends.hasNext()) {
            HasMore(friends.nextCursor)
        } else {
            CursorEnd
        }
    }

    fun hasUsers(): Boolean {
        return friendsRepository.count() > 0
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
                profile.screenName)
    }

}