package io.paulocosta.twitbooks.entity

import javax.persistence.*

enum class Status {
    SUCCESS, FAILED, ABSENT
}

@Entity
data class FriendSyncStatus(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Enumerated(value = EnumType.STRING)
        val status: Status,

        var cursorId: Long? = null
)
