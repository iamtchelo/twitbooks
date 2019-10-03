package io.paulocosta.twitbooks.controller.v1

import io.paulocosta.twitbooks.sync.SyncService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sync")
class TriggerSyncController(private val syncService: SyncService) {

    @GetMapping
    fun triggerSync() = syncService.sync()

}