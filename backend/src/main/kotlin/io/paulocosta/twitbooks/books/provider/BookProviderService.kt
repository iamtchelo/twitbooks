package io.paulocosta.twitbooks.books.provider

import io.reactivex.Single

interface BookProviderService {

    fun search(text: String): Single<BookProviderResponse?>

    val provider: Provider

}