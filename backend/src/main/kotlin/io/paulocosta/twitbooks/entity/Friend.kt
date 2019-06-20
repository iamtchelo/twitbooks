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
        val id: Long? = null,

        val twitterId: Long,

        val name: String,

        val screenName: String,

        val profileImageUrl: String,

        @Enumerated(EnumType.STRING)
        val messageSyncStrategy: MessageSyncStrategy,

        @ManyToMany(mappedBy = "friends")
        var users: Set<User>
)