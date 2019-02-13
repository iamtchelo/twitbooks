package io.paulocosta.twitbooks.entity

import java.time.Instant
import javax.persistence.*

enum class Status {
    SUCCESS, RATE_LIMITED, FAILED
}

@Entity
data class FriendSyncStatus(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Enumerated(value = EnumType.STRING)
        val status: Status,

        val syncWhen: Instant,

        var cursorId: Long? = null
)
