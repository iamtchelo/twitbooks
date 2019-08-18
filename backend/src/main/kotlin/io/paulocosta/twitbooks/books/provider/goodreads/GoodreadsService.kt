package io.paulocosta.twitbooks.books.provider.goodreads

import io.paulocosta.twitbooks.books.provider.BookProviderResponse
import io.paulocosta.twitbooks.books.provider.BookProviderService
import io.reactivex.Single
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("goodreads")
class GoodreadsService(private val goodreadsSearch: GoodreadsSearch) : BookProviderService {

    override fun search(text: String): Single<BookProviderResponse> {
        return Single.never()
    }

//    fun search(search: String): Single<GoodreadsResponse> {
//        return goodreadsSearch.search(search)
//    }

}