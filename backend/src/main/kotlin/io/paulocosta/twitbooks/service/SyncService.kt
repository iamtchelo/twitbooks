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

    @Value("\${twitter.sync.enabled}")
    var twitterSyncEnabled: Boolean = false

    @Value("\${book.sync.enabled}")
    var bookSyncEnabled: Boolean = false


    @Scheduled(fixedDelay = SYNC_DELAY_MILLIS)
    fun sync() {
        twitterSync()
        bookSync()
    }

    private fun twitterSync() {
        if (twitterSyncEnabled) {
            logger.info { "Starting Twitter Sync" }
            twitterSyncService.sync()
        } else {
            logger.info { "Twitter sync disabled by config property" }
        }
    }

    private fun bookSync() {
        if (bookSyncEnabled) {
            bookSyncService.sync()
        } else {
            logger.info { "Book sync disabled by config property" }
        }
    }

}