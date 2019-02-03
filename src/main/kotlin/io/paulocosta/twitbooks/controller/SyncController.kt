package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.service.SyncMessagesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync")
class SyncController @Autowired constructor(val syncMessagesService: SyncMessagesService) {

    @GetMapping
    fun sync(): String {
        syncMessagesService.sync()
        return "OK"
    }

}