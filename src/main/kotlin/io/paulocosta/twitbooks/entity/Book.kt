package io.paulocosta.twitbooks.entity

import javax.persistence.*

@Entity
data class Book(
        @Id
        var id: Long? = null,

        @ManyToMany
        @JoinTable(
                name = "book_matches",
                joinColumns = [JoinColumn(name="message_id")],
                inverseJoinColumns = [JoinColumn(name="book_id")]
        )
        var message: List<Message> = emptyList()
)