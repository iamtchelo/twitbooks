package io.paulocosta.twitbooks.entity

import javax.persistence.*

@Entity
@Table(name = "friends")
data class Friend(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        val twitterId: Long,
        val name: String,
        val screenName: String
)