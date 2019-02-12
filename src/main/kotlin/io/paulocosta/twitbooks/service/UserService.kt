package io.paulocosta.twitbooks.service

import arrow.core.Either
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

object RateLimitError

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
        return when (iterateCursor()) {
            RateLimited -> SyncStatus.FAIL
            CursorEnd -> SyncStatus.SUCCESS
            is HasMore -> SyncStatus.FAIL
        }
    }

    private fun iterateCursor(cursorId: Long? = null): FriendRequestStatus {
        val response = handleFriendsResponse(getNextFriendRequest(cursorId))
        return when (response) {
            is RateLimited -> RateLimited
            is HasMore -> iterateCursor(response.cursorId)
            is CursorEnd -> CursorEnd
        }
    }

    private fun getNextFriendRequest(cursorId: Long?): Either<CursoredList<TwitterProfile>, RateLimitError> {
        val rateLimit = rateLimitService.getFriendRateLimits()
        return when (rateLimit.exceeded()) {
            true -> {
                logger.info { "Rate limit exceeded on friends API" }
                Either.right(RateLimitError)
            }
            false -> {
                return when (cursorId) {
                    null -> Either.left(twitterProvider.getTwitter().friendOperations().friends)
                    else -> Either.left(twitterProvider.getTwitter().friendOperations().getFriendsInCursor(cursorId))
                }
            }
        }
    }

    private fun handleFriendsResponse(response: Either<CursoredList<TwitterProfile>, RateLimitError>): FriendRequestStatus {
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
            is Either.Right -> {
                RateLimited
            }
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