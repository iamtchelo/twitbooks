package io.paulocosta.twitbooks.controller.v1

import io.paulocosta.twitbooks.ner.NERService
import io.paulocosta.twitbooks.books.BookService
import io.paulocosta.twitbooks.service.MessageService
import io.reactivex.Single
import io.reactivex.functions.Function3
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class Status(val nerApiStatus: String, val nerProvider: String, val messageCount: Long, val bookCount: Long)

@RestController
@RequestMapping("/api/v1/status")
class StatusController (
        val messageService: MessageService,
        val nerService: NERService,
        val bookService: BookService) {

    @GetMapping
    fun getStatus(): Single<Status> {
        return Single.zip(bookService.getBookCount(), messageService.getCount(), nerService.getStatus(), Function3 {
            bookCount, messageCount, status ->
            Status(status.name, nerService.providerName, messageCount, bookCount)
        })
    }

}
