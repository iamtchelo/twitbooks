package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.MessageSyncStatus
import org.springframework.data.jpa.repository.JpaRepository

interface MessageSyncStatusRepository : JpaRepository<MessageSyncStatus, Long> {

    fun findFirstByFriendId(friendId: Long): MessageSyncStatus?

}