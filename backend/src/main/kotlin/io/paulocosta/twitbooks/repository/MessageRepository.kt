package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {

    @Query("update Message message set message.processed = true where message.id = :messageId")
    @Modifying
    fun toggleProcessed(messageId: Long)

    @Query("select message from Message message where message.friend.id = ?1 order by message.id asc")
    fun getOldestMessages(friendId: Long, pageable: Pageable): Page<Message>

    @Query("select message from Message message where message.friend.id = ?1 order by message.id desc")
    fun getNewestMessages(friendId: Long, pageable: Pageable): Page<Message>

    @Query("select * from messages where messages.friend_id = :friendId and messages.processed = false", nativeQuery = true)
    fun getUnprocessedMessages(friendId: Long, pageable: Pageable): Page<Message>

    @Query("""
        select * from messages
        INNER JOIN book_matches on messages.id = book_matches.message_id
        WHERE book_matches.book_id = :bookId
    """, nativeQuery = true)
    fun getMessagesByBookId(bookId: Long, pageable: Pageable): Page<Message>

}
