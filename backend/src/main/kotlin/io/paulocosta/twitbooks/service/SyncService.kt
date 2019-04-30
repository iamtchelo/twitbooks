package io.paulocosta.twitbooks.service

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SyncService @Autowired constructor(
        private val twitterSyncService: TwitterSyncService,
        private val bookSyncService: BookSyncService) {

    companion object {
        // One hour
        const val SYNC_DELAY_MILLIS = 3600000L
    }

    @Value("\${sync.enabled}")
    var syncEnabled: Boolean = false

    @Scheduled(fixedDelay = SYNC_DELAY_MILLIS)
    fun sync() {
        if (syncEnabled) {
            logger.info { "Starting Sync" }
            twitterSyncService.sync()
            bookSyncService.sync()
        } else {
            logger.info { "Sync disabled by config" }
        }
    }

}