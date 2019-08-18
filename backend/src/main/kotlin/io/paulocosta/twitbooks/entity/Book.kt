package io.paulocosta.twitbooks.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "books")
data class Book(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        val key: String,

        val title: String,

        val smallImageUrl: String,

        val imageUrl: String,

        val ignored: Boolean = false,

        @CreatedDate
        var createdDate: Date? = null,

        @ManyToMany
        @JoinTable(
                name = "book_matches",
                joinColumns = [JoinColumn(name="book_id")],
                inverseJoinColumns = [JoinColumn(name="message_id")]
        )
        var message: Set<Message> = emptySet(),

        @ManyToMany
        @JoinTable(
                name = "book_user",
                joinColumns = [JoinColumn(name = "book_id")],
                inverseJoinColumns = [JoinColumn(name = "user_id")]
        )
        var users: Set<User> = emptySet()
)