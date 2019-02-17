package io.paulocosta.twitbooks.entity

import javax.persistence.*

enum class MessageSyncStrategy {
        DEPTH, NEWEST
}

@Entity
data class MessageSyncStatus(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,
        @OneToOne
        @JoinColumn(name = "friend_id")
        val friend: Friend,
        @Enumerated(EnumType.STRING)
        val messageSyncStrategy: MessageSyncStrategy
)
