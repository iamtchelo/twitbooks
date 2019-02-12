package io.paulocosta.twitbooks.service

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
        val messageService: MessageService) {

    fun sync() {
        logger.info { "Starting sync" }
        syncUsers()
/*        val users = friendsRepository.findAll()

        users.forEach {
            val messages = messageService.getMessagesFromUser(it)
            messageRepository.saveAll(messages)
        }*/

    }

    private fun syncUsers() {
        logger.info { "Syncing users" }
         when (userService.hasUsers()) {
             true -> logger.info { "Users already present. Skipping user sync!" }
             false -> {
                logger.info { "No users found. Fetching them now!" }
                 userService.syncUsers()
             }
         }
    }

}