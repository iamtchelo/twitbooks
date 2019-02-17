package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {

    @Query("select message from Message message where message.friend.id = ?1 order by message.twitterId asc")
    fun getMessages(friendId: Long, pageable: Pageable): Page<Message>

}