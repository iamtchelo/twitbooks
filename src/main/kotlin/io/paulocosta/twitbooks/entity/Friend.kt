package io.paulocosta.twitbooks.entity

import javax.persistence.*

@Entity
@Table(name = "friends")
data class Friend(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,
        val twitterId: Long,
        val name: String,
        val screenName: String
)