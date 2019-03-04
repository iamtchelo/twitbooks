package io.paulocosta.twitbooks.service

import opennlp.tools.util.normalizer.TwitterCharSequenceNormalizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MessageProcessingService @Autowired constructor(
        val messageService: MessageService,
        val userService: UserService) {

    @Value("\${clear.data}")
    var clearData: Boolean = false

    fun process() {
        val users = userService.getAllUsers()
        users.forEach { friend ->
            val page = messageService.getAllMessages(friend.id, PageRequest.of(1, 100))
            val messages = page.content
            messages.forEach {
                val normalized = TwitterCharSequenceNormalizer.getInstance().normalize(it.text)
                normalized
            }
        }
    }

}