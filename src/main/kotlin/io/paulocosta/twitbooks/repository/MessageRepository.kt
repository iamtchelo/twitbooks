package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {

    fun findFirstByFriendIdOrderByCreatedAt(friendId: Long): Message?

}