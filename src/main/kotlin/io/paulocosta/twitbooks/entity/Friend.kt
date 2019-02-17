package io.paulocosta.twitbooks.entity

import javax.persistence.*

enum class MessageSyncStrategy {
        DEPTH, NEWEST
}

@Entity
@Table(name = "friends")
data class Friend(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        val twitterId: Long,
        val name: String,
        val screenName: String,
        @Enumerated(EnumType.STRING)
        val messageSyncStrategy: MessageSyncStrategy
)