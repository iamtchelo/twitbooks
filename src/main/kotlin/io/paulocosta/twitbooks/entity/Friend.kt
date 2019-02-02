package io.paulocosta.twitbooks.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "friends")
data class Friend(
        @Id
        val id: Long,
        val twitterId: Long,
        val name: String,
        val screenName: String
)