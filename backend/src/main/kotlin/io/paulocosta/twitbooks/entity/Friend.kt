package io.paulocosta.twitbooks.entity

import javax.persistence.*

enum class MessageSyncStrategy {
        DEPTH, NEWEST
}

@Entity
@Table(name = "friends")
data class Friend(
        @Id
        val id: Long? = null,

        val name: String,

        val screenName: String,

        val profileImageUrl: String,

        @Enumerated(EnumType.STRING)
        val messageSyncStrategy: MessageSyncStrategy,

        @ManyToMany(
                mappedBy = "friends",
                cascade = [CascadeType.MERGE])
        var users: Set<User>
)