package io.paulocosta.twitbooks.entity

import javax.persistence.*

data class TwitterApiCredentials(val accessToken: String?, val accessTokenSecret: String?)

@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long?,

        val twitterId: String,

        var accessToken: String?,

        var accessTokenSecret: String?,

        @ManyToMany(mappedBy = "users")
        var books: Set<Book> = emptySet()

) {
        fun getTwitterCredentials(): TwitterApiCredentials {
                return TwitterApiCredentials(this.accessToken, this.accessTokenSecret)
        }
}