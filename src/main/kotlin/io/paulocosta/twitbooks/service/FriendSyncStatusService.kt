package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.FriendSyncStatus
import io.paulocosta.twitbooks.entity.Status
import io.paulocosta.twitbooks.repository.FriendSyncStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class FriendSyncStatusService @Autowired constructor(val friendSyncStatusRepository: FriendSyncStatusRepository) {

    fun getLastestFriendSyncStatus(): FriendSyncStatus? = friendSyncStatusRepository.findFirstByOrderBySyncWhenDesc()

    fun createSyncFailedEvent() {
        val status = FriendSyncStatus(null, Status.FAILED, Instant.now(), null)
        friendSyncStatusRepository.save(status)
    }

    fun createRateLimitEvent(cursorId: Long?) {
        val status = FriendSyncStatus(null, Status.RATE_LIMITED, Instant.now(), cursorId)
        friendSyncStatusRepository.save(status)
    }

    fun createSuccessEvent() {
        val status = FriendSyncStatus(null, Status.SUCCESS, Instant.now(), null)
        friendSyncStatusRepository.save(status)
    }

}