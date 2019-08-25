package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.books.BookService
import io.paulocosta.twitbooks.books.provider.BookProviderResponse
import io.paulocosta.twitbooks.books.provider.BookProviderService
import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.extensions.fdiv
import io.paulocosta.twitbooks.ner.NERService
import io.reactivex.Observable
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit
import javax.transaction.Transactional
import kotlin.math.ceil

private val logger = KotlinLogging.logger {}

data class SyncResponse(
        val providerResponse: BookProviderResponse,
        val message: Message,
        val entities: List<String>
)

@Service
class BookSyncService(
        val messageService: MessageService,
        val friendService: FriendService,
        val nerService: NERService,
        val bookProviderService: BookProviderService,
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
            val pageCount = ceil(messageCount.fdiv(pageSize)).toInt()
            for (currentPage in 1 until pageCount) {
                logger.info { "Progress $currentPage/$pageCount" }
                val page = messageService.getUnprocessedMessages(friendId, PageRequest.of(currentPage, pageCount))
                val messages = page.content
                Observable.fromIterable(messages)
                        .flatMapSingle { nerService.detectEntities(it.text!!).map { entities -> Pair(it, entities) } }
                        .doOnNext {
                            logger.info { "No Entities found, toggling message as processed." }
                            if (it.second.isEmpty()) { toggleMessageProcessed(it.first)}
                        }
                        .filter { it.second.isNotEmpty() }
                        .debounce(1, TimeUnit.SECONDS)
                        .flatMapSingle { bookProviderService.search(it.second[0]).map { res -> SyncResponse(res, it.first, it.second) } }
                        .subscribe(
                                {
                                    processBookIntegrationResponse(it.providerResponse, it.message, it.entities, user)},
                                {
                                    logger.error(it.message)
                                }
                        )
            }
        }
    }

    @Transactional
    fun processBookIntegrationResponse(response: BookProviderResponse?, message: Message, entities: List<String>, user: User) {
        logger.info { "Processing response" }
        if (response != null) {
            val resultBook = response.result
            when (val existingBook = bookService.findByProvider(resultBook.key, resultBook.providers.elementAt(0).id)) {
                null -> {
                    val book = Book(
                            id = existingBook?.id ?: 0,
                            key = resultBook.id.toString(),
                            title = resultBook.title,
                            smallImageUrl = resultBook.smallImageUrl,
                            imageUrl = resultBook.imageUrl,
                            message = setOf(message),
                            users = setOf(user))
                    bookService.saveBook(book)
                    toggleMessageProcessed(message)
                    logger.info { "Book created" }
                }
                else -> {
                    logger.info { "Book already existed. Updating" }
                    bookService.updateBook(existingBook, setOf(message), setOf(user))
                    toggleMessageProcessed(message)
                }
            }
        } else {
            logger.info { "No matches. Marking book as processed" }
            toggleMessageProcessed(message)
        }
    }

    fun toggleMessageProcessed(message: Message) {
        logger.info { "Deleting message with id ${message.id }" }
        messageService.deleteMessage(message.id)
    }

}
