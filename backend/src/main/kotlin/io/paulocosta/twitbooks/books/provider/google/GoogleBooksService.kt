package io.paulocosta.twitbooks.books.provider.google

import io.paulocosta.twitbooks.books.provider.BookProviderResponse
import io.paulocosta.twitbooks.books.provider.BookProviderService
import io.paulocosta.twitbooks.books.provider.Provider
import io.reactivex.Single
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("google-books")
class GoogleBooksService : BookProviderService() {

    override fun getBooks(text: String): Single<BookProviderResponse> {
        return Single.never()
    }

    override val provider: Provider = Provider.GOOGLE_BOOKS

}
