package io.paulocosta.twitbooks.entity

import javax.persistence.Entity

enum class Status {
    DONE, PENDING
}

@Entity
data class SyncStatus(val status: Status = Status.PENDING)

