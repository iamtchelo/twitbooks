package io.paulocosta.twitbooks.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.Twitter
import org.springframework.social.twitter.api.impl.TwitterTemplate
import org.springframework.stereotype.Component

@Component
class TwitterAuth {

    @Autowired
    lateinit var secrets: TwitterAuthSecrets

    private var template: TwitterTemplate? = null

    private fun auth(): TwitterTemplate {
        return TwitterTemplate(
                secrets.consumerKey,
                secrets.consumerSecret,
                secrets.accessToken,
                secrets.accessTokenSecret
        )
        //return TwitterTemplate(secrets.consumerKey, secrets.consumerSecret)
    }

    fun getTwitter(): Twitter {
        return template ?: auth()
    }

}