package io.paulocosta.twitbooks.entity

import javax.persistence.*

@Entity
data class MessageSyncState(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @OneToOne
        @JoinColumn
        val friend: Friend,

        val minId: Long,

        val maxId: Long
)