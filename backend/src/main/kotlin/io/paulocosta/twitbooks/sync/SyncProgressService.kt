package io.paulocosta.twitbooks.sync

import io.paulocosta.twitbooks.auth.SecurityHelper
import io.paulocosta.twitbooks.service.FriendService
import io.reactivex.Single
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

typealias TwitterId = String

@Service
class SyncProgressService constructor(private val friendService: FriendService) {

    val logger = KotlinLogging.logger {}

    private val cache: HashMap<TwitterId, SyncProgressCacheEntry> = HashMap()

    fun syncProgress(): Single<SyncProgress> {
        val id = SecurityHelper.getTwitterId()
        return if (isCacheValid(id)) {
            Single.just(cache[id]!!.syncProgress)
        } else {
            rebuildCache(id)
        }
    }

    fun isCacheValid(id: TwitterId): Boolean {
        val cacheEntry = cache[id]
        if (cacheEntry != null) {
            val cacheTime = cacheEntry.timestamp
            val now = Instant.now()
            val elapsed = cacheTime.until(now, ChronoUnit.SECONDS)
            logger.info { "$elapsed seconds until next progress sync for user $id" }
            if (elapsed > 10) {
                return true
            }
        }
        return false
    }

    fun rebuildCache(id: TwitterId): Single<SyncProgress> {
        return Single.never()
    }

}
