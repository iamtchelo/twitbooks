package io.paulocosta.twitbooks.controller.v1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync_progress")
class SyncProgressController {

    @GetMapping
    fun syncProgress() {

    }

}
