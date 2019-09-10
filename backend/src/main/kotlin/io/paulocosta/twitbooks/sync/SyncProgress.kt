package io.paulocosta.twitbooks.sync

data class SyncProgress(
        val totalFriends: Long,
        val syncedFriends: Long,
        val totalMessages: Long,
        val syncedMessages: Long,
        val bookCount: Long
)
