package io.paulocosta.twitbooks.controller.debug

import io.paulocosta.twitbooks.sync.TwitterSyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/debug/sync/twitter")
@Profile("dev")
class TwitterSyncController @Autowired constructor(val twitterSyncService: TwitterSyncService) {

    @GetMapping
    fun sync(): String {
//        twitterSyncService.sync()
        return "OK"
    }

}
