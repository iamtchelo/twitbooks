package io.paulocosta.twitbooks.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "messages")
data class Message(
        @Id val id: Long,

        val text: String,

        val retweet: Boolean,

        val createdAt: Date,

        @ManyToOne
        var friend: Friend? = null
)