package io.paulocosta.twitbooks.ner.spacy

data class SpacyNERApiResponse(val entities: List<String>) {
    constructor(): this(emptyList())
}