package io.paulocosta.twitbooks.controller.v1

import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.repository.MessageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import retrofit2.http.GET

@RestController
@RequestMapping("/api/v1/messages")
class MessagesController(val messageRepository: MessageRepository) {

    @GET
    @RequestMapping("/{bookId}")
    fun getMessages(@PathVariable bookId: Long, @RequestParam page: Int?): Page<Message> {
        return messageRepository.getMessagesByBookId(bookId, PageRequest.of(page ?: 0, 50))
    }

}