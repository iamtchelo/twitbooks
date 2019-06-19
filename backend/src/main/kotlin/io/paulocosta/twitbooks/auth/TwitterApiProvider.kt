package io.paulocosta.twitbooks.auth

import io.paulocosta.twitbooks.entity.TwitterApiCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.social.twitter.api.Twitter
import org.springframework.social.twitter.api.impl.TwitterTemplate
import org.springframework.stereotype.Component
import java.lang.IllegalStateException

@Component
class TwitterApiProvider {

    @Value("\${twitter.consumer.key}")
    lateinit var consumerKey: String

    @Value("\${twitter.consumer.secret}")
    lateinit var consumerSecret: String

    fun getTwitter(credentials: TwitterApiCredentials): Twitter {

        val accessToken = credentials.accessToken
                ?: throw IllegalStateException("access_token is mandatory to access the Twitter API")

        val accessTokenSecret = credentials.accessTokenSecret
                ?: throw IllegalStateException("access_token_secret is mandatory to access the Twitter API")


        return TwitterTemplate(
                consumerKey,
                consumerSecret,
                accessToken,
                accessTokenSecret
        )
    }

}