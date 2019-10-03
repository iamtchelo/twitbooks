package io.paulocosta.twitbooks.sync

import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.service.UserService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock

@Service
class SyncService @Autowired constructor(
        private val twitterSyncService: TwitterSyncService,
        private val bookSyncService: BookSyncService,
        private val userService: UserService) {

    companion object {
        // One hour
        const val SYNC_DELAY_MILLIS = 3600000L
    }

    private val logger = KotlinLogging.logger {}

    @Value("\${twitter.sync.enabled}")
    var twitterSyncEnabled: Boolean = false

    @Value("\${book.sync.enabled}")
    var bookSyncEnabled: Boolean = false

    private val reentrantLock = ReentrantLock()

    @Scheduled(fixedDelay = SYNC_DELAY_MILLIS)
    fun sync() {
        reentrantLock.lock()
        try {
            userService.getSyncableUsers().forEach {
                twitterSync(it)
                bookSync(it)
            }
        } finally {
            reentrantLock.unlock()
        }
    }

    private fun twitterSync(user: User) {
        if (twitterSyncEnabled) {
            logger.info { "Starting Twitter Sync" }
            twitterSyncService.sync(user)
        } else {
            logger.info { "Twitter sync disabled by config property" }
        }
    }

    private fun bookSync(user: User) {
        if (bookSyncEnabled) {
            bookSyncService.sync(user)
        } else {
            logger.info { "Book sync disabled by config property" }
        }
    }

}
