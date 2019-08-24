package io.paulocosta.twitbooks.books.provider

import io.reactivex.Single
import opennlp.tools.util.normalizer.TwitterCharSequenceNormalizer

abstract class BookProviderService {

    fun search(text: String): Single<BookProviderResponse> {
        return getBooks(normalize(text))
    }

    abstract fun getBooks(text: String): Single<BookProviderResponse>

    abstract val provider: Provider

    private fun normalize(text: String): String {
        return TwitterCharSequenceNormalizer.getInstance().normalize(text).toString().trim()
    }

}
