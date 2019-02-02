package io.paulocosta.twitbooks.entity

import javax.persistence.*

@Entity
@Table(name = "messages")
data class Message(
        @Id val id: Long,

        val text: String,

        @ManyToOne
        val friend: Friend
)