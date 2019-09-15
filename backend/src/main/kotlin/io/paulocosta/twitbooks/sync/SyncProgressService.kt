package io.paulocosta.twitbooks.sync

import io.paulocosta.twitbooks.books.BookService
import io.paulocosta.twitbooks.service.MessageService
import io.reactivex.Single
import io.reactivex.functions.Function3
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

typealias TwitterId = String

@Service
class SyncProgressService constructor(
        private val messageService: MessageService,
        private val bookService: BookService) {

    val logger = KotlinLogging.logger {}

    private val cache: HashMap<TwitterId, SyncProgressCacheEntry> = HashMap()

    fun syncProgress(userId: TwitterId): Single<SyncProgress> {
        return if (isCacheValid(userId)) {
            Single.just(cache[userId]!!.syncProgress)
        } else {
            rebuildCache(userId)
        }
    }

    private fun isCacheValid(id: TwitterId): Boolean {
        val cacheEntry = cache[id]
        if (cacheEntry != null) {
            val cacheTime = cacheEntry.timestamp
            val now = Instant.now()
            val elapsed = cacheTime.until(now, ChronoUnit.SECONDS)
            logger.info { "$elapsed seconds until next progress sync for user $id" }
            if (elapsed > CACHE_EXPIRATION_ELAPSED_TIME_SECONDS) {
                return false
            }
            return true
        }
        return false
    }

    private fun rebuildCache(userId: TwitterId): Single<SyncProgress> {
        val messageCountSingle = messageService.getCount(userId)
        val processedCountSingle = messageService.getProcessedCount(userId)
        val discoveredBookCountSingle = bookService.getAllBooksCount(userId)
        return Single.zip(messageCountSingle, processedCountSingle, discoveredBookCountSingle
                , Function3<Long, Long, Long, SyncProgress> { messageCount, processedCount, discoveredBooksCount ->
            SyncProgress(messageCount, processedCount, discoveredBooksCount)
        }).doOnSuccess { updateCache(userId, it) }
    }

    fun updateCache(userId: TwitterId, entry: SyncProgress) {
        cache[userId] = SyncProgressCacheEntry(entry, Instant.now())
    }

    companion object {
        const val CACHE_EXPIRATION_ELAPSED_TIME_SECONDS = 10
    }

}
