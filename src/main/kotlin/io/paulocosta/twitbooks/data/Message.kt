package io.paulocosta.twitbooks.data

import javax.persistence.*

@Entity
@Table(name = "messages")
data class Message(
        @Id val id: Long,
        val text: String,
        @ManyToOne
        @JoinColumn(name = "friend_id")
        val friend: Friend)