package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.service.TwitterSyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync/twitter")
class TwitterSyncController @Autowired constructor(val twitterSyncService: TwitterSyncService) {

    @GetMapping
    fun sync(): String {
        twitterSyncService.sync()
        return "OK"
    }

}