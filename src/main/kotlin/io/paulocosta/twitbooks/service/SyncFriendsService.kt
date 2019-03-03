package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.SyncResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SyncFriendsService @Autowired constructor(
        val friendsService: UserService,
        val friendSyncStatusService: FriendSyncStatusService,
        val rateLimitService: RateLimitService
) {

    fun sync(): SyncResult {
        val rateLimit = rateLimitService.getFriendRateLimits()
        return SyncResult.ERROR
    }

}
