package io.paulocosta.twitbooks.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

enum class Status {
    DONE, PENDING
}

@Entity
data class SyncStatus(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long,
        val status: Status = Status.PENDING
)

