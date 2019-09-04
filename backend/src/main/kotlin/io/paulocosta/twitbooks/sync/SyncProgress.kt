package io.paulocosta.twitbooks.sync

data class SyncProgress(
        val totalMessages: Long,
        val messagesProcessed: Long,
        val bookEntriesCount: Long
)
