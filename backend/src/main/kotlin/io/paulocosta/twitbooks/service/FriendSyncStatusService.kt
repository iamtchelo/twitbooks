package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.FriendSyncStatus
import io.paulocosta.twitbooks.entity.Status
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.repository.FriendSyncStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FriendSyncStatusService @Autowired constructor(val friendSyncStatusRepository: FriendSyncStatusRepository) {

    fun getLatestFriendSyncStatus(user: User): FriendSyncStatus {
        return when (val status = friendSyncStatusRepository.findFirstByUserId(user.id)) {
            null -> FriendSyncStatus(status = Status.ABSENT)
            else -> status
        }
    }

    fun createSyncFailedEvent(user: User, cursorId: Long?) {
        val status = FriendSyncStatus(null, Status.FAILED, cursorId, user)
        friendSyncStatusRepository.save(status)
    }

    fun createSuccessEvent(user: User) {
        val status = FriendSyncStatus(null, Status.SUCCESS, null, user)
        friendSyncStatusRepository.save(status)
    }

}