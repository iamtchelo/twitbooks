package io.paulocosta.twitbooks.ner.spacy

import io.paulocosta.twitbooks.ner.NERService
import io.reactivex.Single
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("spacy")
class SpacyNERService(private val spacyNERApiClient: SpacyNERApiClient) : NERService {

    override fun detectEntities(text: String): Single<List<String>> {
        return spacyNERApiClient.process(SpacyNERPayload(text))
                .map { it.entities }
    }

}
