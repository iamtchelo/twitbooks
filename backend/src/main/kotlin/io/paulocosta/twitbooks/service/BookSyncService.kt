package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.extensions.fdiv
import io.paulocosta.twitbooks.goodreads.GoodreadsResponse
import io.paulocosta.twitbooks.goodreads.GoodreadsService
import io.paulocosta.twitbooks.ner.NERService
import io.reactivex.Observable
import mu.KotlinLogging
import opennlp.tools.util.normalizer.TwitterCharSequenceNormalizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
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
        val friendService: FriendService,
        val nerService: NERService,
        val goodreadsService: GoodreadsService,
        val bookService: BookService) {


    companion object {
        const val pageSize: Int = 50
    }

    fun sync(user: User) {
        logger.info { "Starting books sync" }
        process(user)
    }

    fun process(user: User) {
        val users = friendService.getAllFriends(user.id)
        users.forEach { friend ->
            val friendId = friend.id ?: throw IllegalStateException("User not found")
            val messageCount = messageService.getUnprocesedCount(friendId).toInt()
            logger.info { "Processing $messageCount messages from user ${friend.screenName}" }
            val pageCount = Math.ceil(messageCount.fdiv(pageSize)).toInt()
            for (currentPage in 1 until pageCount) {
                logger.info { "Progress $currentPage/$pageCount" }
                val page = messageService.getUnprocessedMessages(friendId, PageRequest.of(currentPage, pageCount))
                val messages = page.content
                Observable.fromIterable(messages)
                        .map { normalizeMessage(it) }
                        .flatMapSingle { nerService.detectEntities(it.text).map { entities -> Pair(it, entities) } }
                        .doOnNext {
                            logger.info { "No Entities found, toggling message as processed." }
                            if (it.second.isEmpty()) { toggleMessageProcessed(it.first)}
                        }
                        .filter { it.second.isNotEmpty() }
                        .debounce(1, TimeUnit.SECONDS)
                        .flatMapSingle { goodreadsService.search(it.second[0]).map { res -> SyncResponse(res, it.first, it.second) } }
                        .subscribe(
                                { processGoodreadsResponse(it.goodreadsResponse, it.message, it.entities, user)},
                                {
                                    logger.error(it.message)
                                }
                        )
            }
        }
    }

    @Transactional
    fun processGoodreadsResponse(goodreadsResponse: GoodreadsResponse, message: Message, entities: List<String>, user: User) {
        val results = goodreadsResponse.search.results.works
        if (!results.isNullOrEmpty() && results.size > 0) {
            val resultBook = results[0].bestGoodreadsBook
            if (!crossValidation(resultBook.title, entities)) {
                toggleMessageProcessed(message)
                return
            }
            if (resultBook != null) {
                when (val existingBook = bookService.findById(resultBook.id)) {
                    null -> {
                        val book = Book(
                                id = resultBook.id,
                                title = resultBook.title,
                                smallImageUrl = resultBook.smallImageUrl,
                                imageUrl = resultBook.imageUrl,
                                message = setOf(message),
                                users = setOf(user))
                        bookService.saveBook(book)
                    }
                    else -> {
                        bookService.updateBook(existingBook, setOf(message), setOf(user))
                    }
                }
            } else {
                logger.info { "Cross validation dismissed books from message ${message.id}" }
                toggleMessageProcessed(message)
            }
        } else {
            toggleMessageProcessed(message)
        }
    }

    fun toggleMessageProcessed(message: Message) {
        logger.info { "Deleting message with id ${message.id }" }
        messageService.deleteMessage(message)
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
