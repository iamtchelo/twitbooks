package io.paulocosta.twitbooks.sync

data class SyncProgress(
        val totalMessages: Long,
        val syncedMessages: Long,
        val bookCount: Long
)
