package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.FriendSyncStatus
import io.paulocosta.twitbooks.entity.Status
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.repository.FriendSyncStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class FriendSyncStatusService @Autowired constructor(val friendSyncStatusRepository: FriendSyncStatusRepository) {

    fun getLatestFriendSyncStatus(user: User): FriendSyncStatus {
        val userId = user.id ?: throw IllegalStateException("User not found")
        return when (val status = friendSyncStatusRepository.findFirstByUserIdOrderByIdDesc(userId)) {
            null -> FriendSyncStatus(status = Status.ABSENT)
            else -> status
        }
    }

    fun createSyncFailedEvent(user: User, cursorId: Long?) {
        val status = FriendSyncStatus(null, Status.FAILED, cursorId, user)
        friendSyncStatusRepository.save(status)
    }

    fun createSuccessEvent(user: User, cursorId: Long?) {
        val status = FriendSyncStatus(null, Status.SUCCESS, cursorId, user)
        friendSyncStatusRepository.save(status)
    }

}