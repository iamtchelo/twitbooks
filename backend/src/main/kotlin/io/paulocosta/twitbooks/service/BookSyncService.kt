package io.paulocosta.twitbooks.service

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.paulocosta.twitbooks.books.BookService
import io.paulocosta.twitbooks.books.provider.BookProviderResponse
import io.paulocosta.twitbooks.books.provider.BookProviderService
import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.ner.NERService
import io.reactivex.Observable
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit
import javax.transaction.Transactional

private val logger = KotlinLogging.logger {}

data class SyncResponse(
        val providerResponse: Option<BookProviderResponse>,
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
        const val PAGE_SIZE: Int = 50
    }

    fun sync(user: User) {
        logger.info { "Starting books sync" }
        process(user)
    }

    fun process(user: User) {
        val friends = friendService.getAllFriends(user.id)
        logger.info { "Friend count: ${friends.size}" }
        friends.forEach { friend ->
            val friendId = friend.id ?: throw IllegalStateException("User not found")
            var processed = false
            var pageRequest: Pageable = PageRequest.of(0, PAGE_SIZE)
            logger.info { "Starting to process messages for user ${friend.name}" }
            while (!processed) {
                val page = messageService.getUnprocessedMessages(friendId, pageRequest)
                logger.info { "Processing page ${page.number + 1} of ${page.totalPages}. ${page.totalElements} elements" }
                processPage(friendId, user, page.content)
                processed = page.isLast
                pageRequest = pageRequest.next()
            }
            logger.info { "Finished processing messages for user ${friend.name}" }
        }
    }

    fun processPage(friendId: Long, user: User, messages: List<Message>) {
        Observable.fromIterable(messages)
                .flatMapSingle { nerService.detectEntities(it.text!!).map { entities -> Pair(it, entities) } }
                .doOnNext {
                    logger.info { "No Entities found, toggling message as processed." }
                    if (it.second.isEmpty()) { toggleMessageProcessed(it.first)}
                }
                .filter { it.second.isNotEmpty() }
                .debounce(1, TimeUnit.SECONDS)
                .flatMapSingle {
                    bookProviderService.search(it.second[0]).map { res -> SyncResponse(res, it.first, it.second) }
                }
                .subscribe(
                        {
                            processBookIntegrationResponse(it.providerResponse, it.message, it.entities, user)},
                        {
                            logger.error(it.message, it.stackTrace)
                        }
                )
    }

    @Transactional
    fun processBookIntegrationResponse(response: Option<BookProviderResponse>, message: Message, entities: List<String>, user: User) {
        logger.info { "Processing response" }
        when (response) {
            is Some -> {
                val resultBook = response.t.result
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
            }
            is None -> {
                logger.info { "No matches. Marking book as processed" }
                toggleMessageProcessed(message)
            }
        }
    }

    fun toggleMessageProcessed(message: Message) {
        logger.info { "Deleting message with id ${message.id }" }
        messageService.toggleProcessed(message.id)
    }

}
