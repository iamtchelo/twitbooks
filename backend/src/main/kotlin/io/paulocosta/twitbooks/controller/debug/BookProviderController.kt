package io.paulocosta.twitbooks.controller.debug

import io.paulocosta.twitbooks.books.provider.BookProviderResponse
import io.paulocosta.twitbooks.books.provider.BookProviderService
import io.paulocosta.twitbooks.books.provider.goodreads.GoodreadsResponse
import io.paulocosta.twitbooks.books.provider.goodreads.GoodreadsService
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/debug/search")
@Profile("dev")
class BookProviderController @Autowired constructor(private val bookProviderService: BookProviderService) {

    @GetMapping
    fun search(@RequestParam(name = "q") query: String): Single<BookProviderResponse> {
        return bookProviderService.search(query)
    }

}
