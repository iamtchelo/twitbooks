package io.paulocosta.twitbooks.sync

import java.time.Instant

data class SyncProgressCacheEntry(
        val syncProgress: SyncProgress,
        val timestamp: Instant
)
