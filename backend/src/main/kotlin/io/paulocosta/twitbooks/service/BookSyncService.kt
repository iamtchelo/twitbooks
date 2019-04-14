package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.extensions.fdiv
import io.paulocosta.twitbooks.goodreads.GoodreadsResponse
import io.paulocosta.twitbooks.goodreads.GoodreadsService
import io.paulocosta.twitbooks.nerclient.NERApiPayload
import io.paulocosta.twitbooks.nerclient.NERApiService
import io.reactivex.Observable
import mu.KotlinLogging
import opennlp.tools.util.normalizer.TwitterCharSequenceNormalizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import javax.transaction.Transactional

private val logger = KotlinLogging.logger {}

@Service
class BookSyncService @Autowired constructor(
        val messageService: MessageService,
        val userService: UserService,
        val nerApiService: NERApiService,
        val goodreadsService: GoodreadsService,
        val bookService: BookService) {


    @Value("\${clear.data:false}")
    var clearData: Boolean = false

    companion object {
        const val pageSize: Int = 100
    }

    fun process() {
        val users = userService.getAllUsers()
        users.forEach { friend ->
            val messageCount = messageService.getUnprocessedMessageCount(friend.id)
            logger.info { "Processing $messageCount messages from user ${friend.screenName}" }
            val pageCount = Math.ceil(messageCount.fdiv(pageSize)).toInt()
            for (currentPage in 1 until pageCount) {
                logger.info { "Progress $currentPage/$pageCount" }
                val page = messageService.getAllMessages(friend.id, PageRequest.of(currentPage, pageCount))
                val messages = page.content
                Observable.fromIterable(messages)
                        .map { normalizeMessage(it) }
                        .flatMapSingle { nerApiService.process(NERApiPayload(it.text)).map { entities -> Pair(it, entities) } }
                        .filter { it.second.entities.isNotEmpty() }
                        .debounce(1, TimeUnit.SECONDS)
                        .flatMapSingle { goodreadsService.search(it.second.entities[0]).map { res -> Pair(res, it.first) } }
                        .subscribe(
                                { processGoodreadsResponse(it.first, it.second)},
                                {
                                    logger.error(it.message)
                                }
                        )
            }
        }
    }

    @Transactional
    fun processGoodreadsResponse(goodreadsResponse: GoodreadsResponse, message: Message) {
        val results = goodreadsResponse.search.results.works
        if (!results.isNullOrEmpty() && results.size > 0) {
            val resultBook = results[0].bestGoodreadsBook
            if (resultBook != null) {
                val existingBook = bookService.findById(resultBook.id)
                when (existingBook) {
                    null -> {
                        val book = Book(
                                id = resultBook.id,
                                title = resultBook.title,
                                smallImageUrl = resultBook.smallImageUrl,
                                imageUrl = resultBook.imageUrl)
                        bookService.saveBook(book)
                        bookService.updateMessages(book, setOf(message))
                    }
                    else -> {
                        bookService.updateMessages(existingBook, existingBook.message.plus(message))
                    }
                }
            } else {
                logger.info { "Cross validation dismissed books from message ${message.id}" }
            }
        }
        toggleMessageProcessed(message)
    }

    fun toggleMessageProcessed(message: Message) {
        val processedMessage = message.copy(processed = true)
        messageService.update(processedMessage)
    }

    fun normalizeMessage(message: Message): Message {
        return message.copy(text = TwitterCharSequenceNormalizer.getInstance().normalize(message.text).toString().trim())
    }

}