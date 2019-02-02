package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {

    @Query("SELECT twitterId FROM Message WHERE friend.id = ?1 ORDER BY createdAt ASC")
    fun findOldestMessage(friendId: Long): Message?

}