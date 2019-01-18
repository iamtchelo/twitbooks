package io.paulocosta.twitbooks.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.social.UserIdSource
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer
import org.springframework.social.config.annotation.SocialConfigurer
import org.springframework.social.connect.ConnectionFactoryLocator
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.social.twitter.connect.TwitterConnectionFactory

@Configuration
class SocialConfig : SocialConfigurer {

    @Autowired
    lateinit var twitterConfig: TwitterAuthSecrets

    override fun getUsersConnectionRepository(p0: ConnectionFactoryLocator?): UsersConnectionRepository {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserIdSource(): UserIdSource {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addConnectionFactories(
            connectionFactoryConfigurer: ConnectionFactoryConfigurer,
            environment: Environment) {
        connectionFactoryConfigurer.addConnectionFactory(
                TwitterConnectionFactory(
                        twitterConfig.consumerKey,
                        twitterConfig.consumerSecret
                )
        )
    }

}