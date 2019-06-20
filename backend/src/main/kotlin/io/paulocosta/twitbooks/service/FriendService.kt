package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Friend
import io.paulocosta.twitbooks.entity.MessageSyncStrategy
import io.paulocosta.twitbooks.repository.FriendsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class FriendService @Autowired constructor(
        private val friendsRepository: FriendsRepository) {

    fun getAllFriends(): List<Friend> = friendsRepository.findAll()

    @Transactional
    fun saveFriends(friends: List<Friend>): List<Friend> = friendsRepository.saveAll(friends)

    @Transactional
    fun updateMessageSyncMode(friend: Friend, messageSyncStrategy: MessageSyncStrategy) {
        friendsRepository.save(friend.copy(messageSyncStrategy = messageSyncStrategy))
    }

}