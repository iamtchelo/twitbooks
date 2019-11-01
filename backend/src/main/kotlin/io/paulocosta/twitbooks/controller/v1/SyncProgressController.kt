package io.paulocosta.twitbooks.controller.v1

import io.paulocosta.twitbooks.auth.SecurityHelper
import io.paulocosta.twitbooks.sync.SyncProgress
import io.paulocosta.twitbooks.sync.SyncProgressService
import io.reactivex.Single
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sync_progress")
class SyncProgressController(private val securityHelper: SecurityHelper,
                             private val syncProgressService: SyncProgressService) {

    @GetMapping
    fun syncProgress(): Single<SyncProgress> {
        val userId = securityHelper.getTwitterId()
        return syncProgressService.syncProgress(userId)
    }

}
