package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Friend
import io.paulocosta.twitbooks.entity.MessageSyncState
import io.paulocosta.twitbooks.repository.MessageSyncStateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageSyncStateService @Autowired constructor(
        private val messageSyncStateRepository: MessageSyncStateRepository) {

    fun getMessageSyncState(friendId: Long): MessageSyncState? {
        return messageSyncStateRepository.getFirstByFriendId(friendId)
    }

    fun saveMessageSyncState(friend: Friend, maxId: Long, minId: Long) {

        val existing = getMessageSyncState(friend.id)

        val updated = existing?.copy(maxId = maxId, minId = minId)
                ?: MessageSyncState(friend = friend, maxId = maxId, minId = minId)

        messageSyncStateRepository.save(updated)
    }

}