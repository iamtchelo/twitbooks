package io.paulocosta.twitbooks.entity

import javax.persistence.*

@Entity
@Table(name = "books")
data class Book(
        @Id
        var id: Long? = null,

        val title: String,

        val smallImageUrl: String,

        val imageUrl: String,

        @ManyToMany
        @JoinTable(
                name = "book_matches",
                joinColumns = [JoinColumn(name="book_id")],
                inverseJoinColumns = [JoinColumn(name="message_id")]
        )
        var message: Set<Message> = emptySet()
)