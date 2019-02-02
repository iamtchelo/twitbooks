package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.service.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync")
class MessagesController @Autowired constructor(val messageService: MessageService) {

    @GetMapping
    fun get(): List<Message> {
        return messageService.getMessagesFromUser(25103L, "jessitron")
    }

}