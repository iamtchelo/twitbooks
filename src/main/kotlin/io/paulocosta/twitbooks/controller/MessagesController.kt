package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.entity.Friend
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.service.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/messages")
class MessagesController @Autowired constructor(val messageService: MessageService) {

    @GetMapping
    fun get(): List<Message> {
        val friend = Friend(1, 25103, "Jess", "jessitron")
        return messageService.getMessagesFromUser(friend)
    }

}