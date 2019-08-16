package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.ner.spacy.SpacyNERApiClient
import io.paulocosta.twitbooks.service.BookService
import io.paulocosta.twitbooks.service.MessageService
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class Status(val nerApiStatus: String, val messageCount: Long, val bookCount: Long)

@RestController
@RequestMapping("/status")
@CrossOrigin("*")
class StatusController @Autowired constructor(
        val messageService: MessageService,
        val spacyNerApiClient: SpacyNERApiClient,
        val bookService: BookService) {

    @GetMapping
    fun getStatus(): Single<Status> {
        val bookCount = bookService.getBookCount()
        val messageCount = messageService.getCount()
        return spacyNerApiClient.status()
                .toSingle { Status("ONLINE", messageCount, bookCount) }
                .onErrorReturn { Status("OFFLINE", messageCount, bookCount) }
    }

}
