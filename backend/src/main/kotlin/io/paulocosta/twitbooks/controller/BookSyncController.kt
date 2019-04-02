package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.service.BookSyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("process")
class BookSyncController @Autowired constructor(val bookSyncService: BookSyncService) {

    @GetMapping
    fun test() {
        bookSyncService.process()
    }

}