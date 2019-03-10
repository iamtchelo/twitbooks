package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.FriendSyncStatus
import io.paulocosta.twitbooks.entity.Status
import io.paulocosta.twitbooks.repository.FriendSyncStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FriendSyncStatusService @Autowired constructor(val friendSyncStatusRepository: FriendSyncStatusRepository) {

    fun getLatestFriendSyncStatus(): FriendSyncStatus {
        val status = friendSyncStatusRepository.findFirstByOrderByIdDesc()
        return when (status) {
            null -> FriendSyncStatus(status = Status.ABSENT)
            else -> status
        }
    }

    fun createSyncFailedEvent(cursorId: Long?) {
        val status = FriendSyncStatus(null, Status.FAILED, cursorId)
        friendSyncStatusRepository.save(status)
    }

    fun createSuccessEvent() {
        val status = FriendSyncStatus(null, Status.SUCCESS, null)
        friendSyncStatusRepository.save(status)
    }

}