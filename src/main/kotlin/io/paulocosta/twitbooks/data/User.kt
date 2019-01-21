package io.paulocosta.twitbooks.data

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "friends")
data class Friend(
        @Id
        val id: Long
)