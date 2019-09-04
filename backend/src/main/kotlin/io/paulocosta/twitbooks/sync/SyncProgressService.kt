package io.paulocosta.twitbooks.sync

import org.springframework.stereotype.Service

typealias TwitterId = String

@Service
class SyncProgressService {

    private val cache: HashMap<TwitterId, SyncProgressCacheEntry> = HashMap()

}
