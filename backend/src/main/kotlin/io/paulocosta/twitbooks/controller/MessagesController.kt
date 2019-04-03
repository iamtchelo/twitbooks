package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.repository.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import retrofit2.http.GET

@RestController
@RequestMapping("/messages")
class MessagesController @Autowired constructor(val messageRepository: MessageRepository) {

    @GET
    @RequestMapping("/{bookId}")
    fun getMessages(@PathVariable bookId: Long): List<Message> {
        return messageRepository.getMessagesByBookId(bookId)
    }

}