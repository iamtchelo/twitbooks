package io.paulocosta.twitbooks.books.provider.goodreads

import arrow.core.Option
import io.paulocosta.twitbooks.books.provider.BookProviderResponse
import io.paulocosta.twitbooks.books.provider.BookProviderService
import io.paulocosta.twitbooks.books.provider.Provider
import io.paulocosta.twitbooks.entity.Book
import io.reactivex.Single
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.text.Charsets.UTF_8

@Service
@Profile("goodreads")
class GoodreadsService(private val goodreadsSearchApi: GoodreadsSearchApi) : BookProviderService() {

    override fun getBooks(text: String): Single<Option<BookProviderResponse>> {
        return goodreadsSearchApi.search(text).map(this::processResponse).delay(1, TimeUnit.SECONDS)
    }

    override val provider: Provider = Provider.GOODREADS

    fun processResponse(response: GoodreadsResponse): Option<BookProviderResponse> {
        val books = response.search?.results?.works?.map {
            it.bestGoodreadsBook
        }
        books?.let { books ->
            if (books.isNotEmpty()) {
                val book = books[0]
                if (book != null) {
                    return Option.just(BookProviderResponse(processBook(book)))
                }
            }
        }
        return Option.empty()
    }

    fun processBook(goodreadsBook: GoodreadsBook): Book {
        return Book(
                id = 0,
                key = goodreadsBook.id.toString(),
                title = goodreadsBook.title!!,
                smallImageUrl = goodreadsBook.smallImageUrl!!,
                imageUrl = goodreadsBook.imageUrl!!,
                ignored = false,
                detailsUrl = URLEncoder.encode("https://www.goodreads.com/book/title?id=${goodreadsBook.title}", UTF_8.name()),
                providers = setOf(provider)
        )
    }

}
