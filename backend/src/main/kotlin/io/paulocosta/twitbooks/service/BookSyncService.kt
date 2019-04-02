package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.extensions.fdiv
import io.paulocosta.twitbooks.goodreads.GoodreadsBook
import io.paulocosta.twitbooks.goodreads.GoodreadsResponse
import io.paulocosta.twitbooks.goodreads.GoodreadsService
import io.reactivex.Observable
import mu.KotlinLogging
import opennlp.tools.ngram.NGramUtils
import opennlp.tools.tokenize.SimpleTokenizer
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
                messages.forEach { message ->
                    val normalizedText = normalizeText(message.text)
                    val tokenizer = SimpleTokenizer.INSTANCE
                    /**
                     * The uppercase filter will greatly reduce the dimension and vastly improve the speed of this thing.
                     **/
                    val tokens = tokenizer.tokenize(normalizedText).filter { it.first().isUpperCase() }.toTypedArray()
                    getNgrams(tokens)
                            .filter { it.isNotBlank() }
                            .debounce(1, TimeUnit.SECONDS)
                            .flatMapSingle { goodreadsService.search(it) }
                            .subscribe({
                                processGoodreadsResponse(it, message, tokens)
                            }, {
                                logger.error(it.message)
                            })
                }
            }
        }
    }

    @Transactional
    fun processGoodreadsResponse(goodreadsResponse: GoodreadsResponse, message: Message, tokens: Array<String>) {
        val results = goodreadsResponse.search.results.works
        if (!results.isNullOrEmpty() && results.size > 0) {
            val resultBook = crossValidation(results.map { it.bestGoodreadsBook }, tokens)
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

    /**
     * Checks if the Goodreads results match exactly a given token name.
     **/
    fun crossValidation(apiBooks: List< GoodreadsBook>, tokens: Array<String>): GoodreadsBook? {
        val filtered = apiBooks.filter { tokens.contains(it.title.trim()) }
        return if (filtered.isEmpty()) {
            null
        } else {
            filtered[0]
        }
    }

    fun toggleMessageProcessed(message: Message) {
        val processedMessage = message.copy(processed = true)
        messageService.update(processedMessage)
    }

    fun getNgrams(tokens: Array<String>): Observable<String> {
        val nGramStreams = (1..tokens.size).map { i ->
            val ngrams = NGramUtils.getNGrams(tokens, i)
            val joinedNgrams = mutableListOf<String>()
            ngrams.forEach {
                joinedNgrams.add(it.joinToString(separator = " "))
            }
            Observable.fromIterable(joinedNgrams)
        }
        return Observable.concat(nGramStreams)
    }

    fun normalizeText(text: String): String {
        return TwitterCharSequenceNormalizer.getInstance().normalize(text).toString().trim()
    }

}