package io.paulocosta.twitbooks.books.provider.google

import arrow.core.Option
import io.paulocosta.twitbooks.books.provider.BookProviderResponse
import io.paulocosta.twitbooks.books.provider.BookProviderService
import io.paulocosta.twitbooks.books.provider.Provider
import io.paulocosta.twitbooks.entity.Book
import io.reactivex.Single
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("google")
class GoogleBooksService(private val googleBooksApi: GoogleBooksApi) : BookProviderService() {

    companion object {
        const val LANGUAGE_RESTRICT = "en"
        const val MAX_RESULTS = 1
    }

    override fun getBooks(text: String): Single<Option<BookProviderResponse>> {
        return googleBooksApi.search(text, LANGUAGE_RESTRICT, MAX_RESULTS)
                .map(this::processResponse)
    }

    fun processResponse(res: GoogleBooksApiResponse): Option<BookProviderResponse> {
        val items = res.items ?: emptyList()
        if (items.isNotEmpty()) {
            return Option.just(BookProviderResponse(toBook(items[0])))
        }
        return Option.empty()
    }

    fun toBook(item: Item): Book {
        return Book(
                id = 0,
                key = item.id,
                title = item.volumeInfo.title,
                smallImageUrl = item.volumeInfo.imageLinks?.smallThumbnail ?: "",
                imageUrl = item.volumeInfo.imageLinks?.thumbnail ?: "",
                ignored = false,
                providers = setOf(provider)
        )
    }

    override val provider: Provider = Provider.GOOGLE_BOOKS

}
