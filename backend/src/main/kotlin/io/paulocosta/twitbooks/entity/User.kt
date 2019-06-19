package io.paulocosta.twitbooks.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

data class TwitterApiCredentials(val accessToken: String?, val accessTokenSecret: String?)

@Entity
@Table(name = "users")
data class User(
        @Id
        val id: String,
        var accessToken: String?,
        var accessTokenSecret: String?
) {
        fun getTwitterCredentials(): TwitterApiCredentials {
                return TwitterApiCredentials(this.accessToken, this.accessTokenSecret)
        }
}