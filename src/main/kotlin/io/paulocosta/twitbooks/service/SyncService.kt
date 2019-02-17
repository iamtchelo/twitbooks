package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Status
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service dedicated to sync messages from friends. I'm still
 * unsure on the exact method of obtaining entity. I could start
 * from the timelines and going down on the tweet chain but I would
 * miss the new tweets that way.
 *
 * On the other hand I could try finding the oldest tweet from the
 * friend and going up from there. But that way I might take too long
 * to synchronize the entity.
 *
 * For an MVP one I think the first approach could be better.
 * **/

private val logger = KotlinLogging.logger {}

@Service
class SyncService @Autowired constructor(
        val userService: UserService,
        val friendSyncStatusService: FriendSyncStatusService,
        val messageService: MessageService) {

    fun sync() {
        logger.info { "Starting sync" }
        syncUsers()
        syncMessages()
    }

    private fun syncMessages() {
        // TODO leaving 0 for now while I figure out message sync
        val user = userService.findFriendById(1)
        messageService.syncMessages(user.get())
    }

    private fun syncUsers() {
        logger.info { "Syncing users" }
        val syncStatus = friendSyncStatusService.getLastestFriendSyncStatus()
        when (syncStatus) {
            null -> handleUserSyncResult(userService.fullSync())
            else -> when (syncStatus.status) {
                Status.SUCCESS -> {
                    logger.info { "Previous sync status was successful. Not syncing users now" }
                }
                Status.RATE_LIMITED -> {
                    logger.info { "Previous user status was rate limit. Attempting to continue sync using previous cursor" }
                    handleUserSyncResult(userService.cursorSync(syncStatus.cursorId))
                }
                Status.FAILED -> {
                    logger.info { "Previous sync failed for some unknown reason. Stopping sync until the issue is known." }
                }
            }
        }
    }

    private fun handleUserSyncResult(result: FriendRequestStatus) {
        when (result) {
            is RateLimited -> {
                logger.info { "User sync request has been rate limited. Saving cursor for later sync attempt" }
                friendSyncStatusService.createRateLimitEvent(result.cursorId)
            }
            is CursorEnd -> {
                logger.info { "Synchronization was successful! Saving status." }
                friendSyncStatusService.createSuccessEvent()
            }
            else -> {
                logger.info { "Something went wrong during sync! Saving status." }
                friendSyncStatusService.createSyncFailedEvent()
            }
        }
    }

}