package io.paulocosta.twitbooks.ner.spacy

import io.paulocosta.twitbooks.ner.NERService
import io.paulocosta.twitbooks.ner.NERServiceStatus
import io.reactivex.Single
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("spacy")
class SpacyNERService(private val spacyNERApiClient: SpacyNERApiClient) : NERService {

    override fun getStatus(): Single<NERServiceStatus> {
        return spacyNERApiClient.status()
                .toSingleDefault(true)
                .map { NERServiceStatus.ONLINE }
                .onErrorReturn { NERServiceStatus.OFFLINE }
    }

    override val providerName: String
        get() = "SpaCy"

    override fun detectEntities(text: String): Single<List<String>> {
        return spacyNERApiClient.process(SpacyNERPayload(text))
                .map { it.entities }
    }

}
