package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.service.MessageProcessingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("process")
class MessageProcessingController @Autowired constructor(val messageProcessingService: MessageProcessingService) {

    @GetMapping
    fun test() {
        messageProcessingService.process()
    }

}