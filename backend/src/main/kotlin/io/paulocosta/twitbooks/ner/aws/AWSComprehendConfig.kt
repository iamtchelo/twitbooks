package io.paulocosta.twitbooks.ner.aws

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.comprehend.AmazonComprehend
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("comprehend")
class AWSComprehendConfig {

    @Value("\${aws.accessKeyId}")
    lateinit var accessKeyId: String

    @Value("\${aws.secretKey}")
    lateinit var secretKey: String

    @Value("\${aws.region}")
    lateinit var region: String

    @Bean
    fun config(): AmazonComprehend {
        val creds = BasicAWSCredentials(accessKeyId, secretKey)
        return AmazonComprehendClientBuilder.standard()
                .withCredentials(AWSStaticCredentialsProvider(creds))
                .withRegion(region)
                .build()
    }

}
