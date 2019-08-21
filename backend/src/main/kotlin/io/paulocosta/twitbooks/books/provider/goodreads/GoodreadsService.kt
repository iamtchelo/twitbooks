package io.paulocosta.twitbooks.books.provider.goodreads

import io.paulocosta.twitbooks.books.provider.BookProviderResponse
import io.paulocosta.twitbooks.books.provider.BookProviderService
import io.paulocosta.twitbooks.books.provider.Provider
import io.paulocosta.twitbooks.entity.Book
import io.reactivex.Single
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("goodreads")
class GoodreadsService(private val goodreadsSearch: GoodreadsSearch) : BookProviderService {

    override val provider: Provider = Provider.GOODREADS

    override fun search(text: String): Single<BookProviderResponse?> {
        return goodreadsSearch.search(text)
                .map(this::processResponse)
    }

    fun processResponse(response: GoodreadsResponse): BookProviderResponse? {
        val books = response.search?.results?.works?.map {
            it.bestGoodreadsBook
        }
        books?.let { books ->
            if (books.isNotEmpty()) {
                return books[0]?.let { BookProviderResponse(processBook(it)) }
            }
        }
        return null
    }

    fun processBook(goodreadsBook: GoodreadsBook): Book {
        return Book(
                id = 0,
                key = goodreadsBook.id.toString(),
                title = goodreadsBook.title!!,
                smallImageUrl = goodreadsBook.smallImageUrl!!,
                imageUrl = goodreadsBook.imageUrl!!,
                ignored = false,
                providers = setOf(provider)
        )
    }

}