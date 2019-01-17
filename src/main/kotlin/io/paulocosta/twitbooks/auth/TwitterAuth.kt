package io.paulocosta.twitbooks.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.impl.TwitterTemplate
import org.springframework.stereotype.Component

@Component
class TwitterAuth {

    @Autowired
    lateinit var secrets: TwitterAuthSecrets

    fun auth(): Boolean {
        val twitterTemplate = TwitterTemplate(
                secrets.consumerKey,
                secrets.consumerSecret,
                secrets.accessToken,
                secrets.accessTokenSecret
        )
        return twitterTemplate.isAuthorized
    }

}