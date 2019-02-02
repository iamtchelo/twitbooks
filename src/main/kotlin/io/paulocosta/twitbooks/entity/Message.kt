package io.paulocosta.twitbooks.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "messages")
data class Message(
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id val id: Long? = null,

        val text: String,

        val twitterId: Long,

        val retweet: Boolean,

        val createdAt: Date,

        @ManyToOne
        var friend: Friend? = null
)