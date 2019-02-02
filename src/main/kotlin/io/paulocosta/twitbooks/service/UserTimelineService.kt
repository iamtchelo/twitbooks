package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Message
import org.springframework.stereotype.Service

@Service
class UserTimelineService {

    fun getMessagesFromUserTimeline(userId: Int): List<Message> {
        return emptyList()
    }

}