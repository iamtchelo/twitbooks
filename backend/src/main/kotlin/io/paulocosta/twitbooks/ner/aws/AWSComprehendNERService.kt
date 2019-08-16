package io.paulocosta.twitbooks.ner.aws

import com.amazonaws.services.comprehend.AmazonComprehend
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest
import io.paulocosta.twitbooks.ner.NERService
import io.reactivex.Single
import org.springframework.stereotype.Service

@Service
class AWSComprehendNERService(private val amazonComprehend: AmazonComprehend) : NERService {

    override fun detectEntities(text: String): Single<List<String>> {
        val request = DetectEntitiesRequest()
                .withText(text)
                .withLanguageCode("en")
        val result = amazonComprehend.detectEntities(request)
        return Single.just(result.entities.map { it.text })
    }

}
