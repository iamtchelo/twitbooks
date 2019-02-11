package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.service.SyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync")
class SyncController @Autowired constructor(val syncService: SyncService) {

    @GetMapping
    fun sync(): String {
        syncService.sync()
        return "OK"
    }

}