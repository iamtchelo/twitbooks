package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.MessageSyncState
import org.springframework.data.jpa.repository.JpaRepository

interface MessageSyncStateRepository : JpaRepository<MessageSyncState, Long> {

    fun getFirstByFriendId(friendId: Long): MessageSyncState?

}