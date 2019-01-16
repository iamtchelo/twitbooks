package io.paulocosta.twitbooks.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.impl.TwitterTemplate
import org.springframework.stereotype.Component

@Autowired
lateinit var secrets: TwitterAuthSecrets

@Component
class TwitterAuth {
    fun auth() {
        val twitterTemplate = TwitterTemplate(
                secrets.consumerKey,
                secrets.consumerSecret,
                secrets.apiKey,
                secrets.apiSecret
        )
        twitterTemplate.searchOperations()
    }

}