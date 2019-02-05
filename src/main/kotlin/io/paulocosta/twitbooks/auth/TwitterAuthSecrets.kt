package io.paulocosta.twitbooks.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TwitterAuthSecrets {

    @Value("\${consumer.key:''}")
    lateinit var consumerKey: String

    @Value("\${consumer.secret:''}")
    lateinit var consumerSecret: String

    @Value("\${access.token:''}")
    lateinit var accessToken: String

    @Value("\${access.token.secret:''}")
    lateinit var accessTokenSecret: String

}