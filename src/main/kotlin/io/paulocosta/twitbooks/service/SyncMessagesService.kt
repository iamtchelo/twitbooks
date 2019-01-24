package io.paulocosta.twitbooks.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service dedicated to sync messages from friends. I'm still
 * unsure on the exact method of obtaining data. I could start
 * from the timelines and going down on the tweet chain but I would
 * miss the new tweets that way.
 *
 * On the other hand I could try finding the oldest tweet from the
 * friend and going up from there. But that way I might take too long
 * to synchronize the data.
 *
 * For an MVP one I think the first approach could be better.
 * **/
@Service
class SyncMessagesService {

    @Autowired
    lateinit var rateLimitService: RateLimitService

}