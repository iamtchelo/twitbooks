package io.paulocosta.twitbooks.ner.aws

import com.amazonaws.services.comprehend.AmazonComprehend
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest
import io.paulocosta.twitbooks.ner.NERService
import io.paulocosta.twitbooks.ner.NERServiceStatus
import io.reactivex.Single
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

val logger = KotlinLogging.logger {}

// NLP requests are measured in units of 100 characters, with a 3 unit (300 character) minimum charge per request.
// Twitter's limit is 280

@Service
@Profile("comprehend")
class AWSComprehendNERService(private val amazonComprehend: AmazonComprehend) : NERService {

    override fun getStatus(): Single<NERServiceStatus> = Single.just(NERServiceStatus.ONLINE)

    override val providerName: String
        get() = "AWSComprehend"

    override fun detectEntities(text: String): Single<List<String>> {
        val request = DetectEntitiesRequest()
                .withText(text)
                .withLanguageCode("en")
        val result = amazonComprehend.detectEntities(request)
        result.entities[0].type
        Thread.sleep(1000)
        logger.info { "Result from AWS Comprehend: $result" }
        return Single.just(result.entities.map { it.text })
    }

}
