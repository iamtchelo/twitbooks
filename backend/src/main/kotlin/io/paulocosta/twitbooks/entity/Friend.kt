package io.paulocosta.twitbooks.entity

import javax.persistence.*

enum class MessageSyncStrategy {
        DEPTH, NEWEST
}

@Entity
@Table(name = "friends")
data class Friend(
        @Id
        val id: Long,
        val name: String,
        val screenName: String,
        @Enumerated(EnumType.STRING)
        val messageSyncStrategy: MessageSyncStrategy,
        @ManyToOne
        @JoinColumn
        var user: User? = null
)