package io.paulocosta.twitbooks.ner.spacy

import io.paulocosta.twitbooks.ner.NERService
import io.reactivex.Single

class SpacyNERService(private val spacyNERApiClient: SpacyNERApiClient) : NERService {

    override fun detectEntities(text: String): Single<List<String>> {
        return spacyNERApiClient.process(text)
    }

}
