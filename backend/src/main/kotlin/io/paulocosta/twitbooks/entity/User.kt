package io.paulocosta.twitbooks.entity

import javax.persistence.Entity
import javax.persistence.Id

//@Entity
data class User(
        @Id
        val id: Long?
)