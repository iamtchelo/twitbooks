package io.paulocosta.twitbooks.entity

import javax.persistence.*

data class TwitterApiCredentials(val accessToken: String?, val accessTokenSecret: String?)

@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        val twitterId: String,

        var accessToken: String?,

        var accessTokenSecret: String?,

        @ManyToMany
        @JoinTable(
                name = "user_friends",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "friend_id")]
        )
        var friends: Set<Friend> = emptySet(),

        @ManyToMany(mappedBy = "users")
        var books: Set<Book> = emptySet()

) {
        fun getTwitterCredentials(): TwitterApiCredentials {
                return TwitterApiCredentials(this.accessToken, this.accessTokenSecret)
        }
}