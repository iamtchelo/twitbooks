package io.paulocosta.twitbooks.controller.debug

import io.paulocosta.twitbooks.sync.BookSyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/debug/sync/books")
@Profile("dev")
class BookSyncController @Autowired constructor(val bookSyncService: BookSyncService) {

    @GetMapping
    fun test() {
        // bookSyncService.process()
    }

}
