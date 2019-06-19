package io.paulocosta.twitbooks.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
data class User(
        @Id
        val id: String,
        val accessToken: String
)