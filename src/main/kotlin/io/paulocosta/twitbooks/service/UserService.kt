package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Friend
import io.paulocosta.twitbooks.entity.MessageSyncStrategy
import io.paulocosta.twitbooks.repository.FriendsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
        private val friendsRepository: FriendsRepository) {

    fun getAllUsers(): List<Friend> = friendsRepository.findAll()

    fun saveFriends(friends: List<Friend>): List<Friend> = friendsRepository.saveAll(friends)

    fun updateMessageSyncMode(friend: Friend, messageSyncStrategy: MessageSyncStrategy) {
        friendsRepository.save(Friend(
                friend.id, friend.name, friend.screenName, messageSyncStrategy
        ))
    }

}