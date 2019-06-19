package io.paulocosta.twitbooks.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.Twitter
import org.springframework.social.twitter.api.impl.TwitterTemplate
import org.springframework.stereotype.Component
import java.lang.IllegalStateException

@Component
class TwitterProvider {

//    @Autowired
//    lateinit var secrets: TwitterAuthSecrets

//    private fun auth(): TwitterTemplate {
//        return TwitterTemplate(
//                secrets.consumerKey,
//                secrets.consumerSecret,
//                secrets.accessToken,
//                secrets.accessTokenSecret
//        )
//    }

    fun getTwitter(secret: String?): Twitter {
        if (secret == null) {
            // TODO throw proper exception
            throw IllegalStateException("User cannot sync without an accessKey")
        }
        return TwitterTemplate(secret)
    }

}