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
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import javax.transaction.Transactional

private val logger = KotlinLogging.logger {}

data class SyncResponse(
        val goodreadsResponse: GoodreadsResponse,
        val message: Message,
        val entities: List<String>)

@Service
class BookSyncService @Autowired constructor(
        val messageService: MessageService,
        val userService: UserService,
        val nerApiService: NERApiService,
        val goodreadsService: GoodreadsService,
        val bookService: BookService) {


    @Value("\${spring.profiles.active}")
    lateinit var activeProfile: String

    @Value("\${sync.enabled}")
    var syncEnabled: Boolean = false

    companion object {
        const val pageSize: Int = 100
        const val SYNC_DELAY_MILLIS = 600000L
    }

    @Scheduled(fixedDelay = SYNC_DELAY_MILLIS)
    fun sync() {
        if (syncEnabled) {
            logger.info { "Starting books sync" }
            process()
        } else {
            logger.info { "Book sync disable by config" }
        }

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
                        .doOnNext {
                            logger.info { "No Entities found, toggling message as processed." }
                            if (it.second.entities.isEmpty()) { toggleMessageProcessed(it.first)}
                        }
                        .filter { it.second.entities.isNotEmpty() }
                        .debounce(1, TimeUnit.SECONDS)
                        .flatMapSingle { goodreadsService.search(it.second.entities[0]).map { res -> SyncResponse(res, it.first, it.second.entities) } }
                        .subscribe(
                                { processGoodreadsResponse(it.goodreadsResponse, it.message, it.entities)},
                                {
                                    logger.error(it.message)
                                }
                        )
            }
        }
    }

    @Transactional
    fun processGoodreadsResponse(goodreadsResponse: GoodreadsResponse, message: Message, entities: List<String>) {
        val results = goodreadsResponse.search.results.works
        if (!results.isNullOrEmpty() && results.size > 0) {
            val resultBook = results[0].bestGoodreadsBook
            if (!crossValidation(resultBook.title, entities)) {
                toggleMessageProcessed(message)
                return
            }
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

        if (activeProfile == "prod") {
            logger.info { "Deleting message with id ${message.id }" }
            messageService.deleteMessage(message)
        } else {
            val processedMessage = message.copy(processed = true)
            messageService.update(processedMessage)
        }

    }

    fun normalizeMessage(message: Message): Message {
        return message.copy(text = TwitterCharSequenceNormalizer.getInstance().normalize(message.text).toString().trim())
    }

    fun crossValidation(apiResponse: String, entities: List<String>): Boolean {
        entities.forEach {
            if (apiResponse.trim().equals(it.trim(), ignoreCase = true)) {
                return true
            }
        }
        return false
    }

}