package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.FriendSyncStatus
import org.springframework.data.jpa.repository.JpaRepository

interface FriendSyncStatusRepository : JpaRepository<FriendSyncStatus, Long> {

    fun findFirstByUserIdOrderByIdDesc(userId: Long): FriendSyncStatus?

}
