package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
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

private val logger = KotlinLogging.logger {}

@Service
class MessageProcessingService @Autowired constructor(
        val messageService: MessageService,
        val userService: UserService,
        val goodreadsService: GoodreadsService,
        val bookService: BookService) {


    @Value("\${clear.data:false}")
    var clearData: Boolean = false

    fun process() {
        val users = userService.getAllUsers()
        users.forEach { friend ->
            // TODO iterate this properly
            val page = messageService.getAllMessages(friend.id, PageRequest.of(1, 100))
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
                        .subscribe { processGoodreadsResponse(it, message) }
            }
        }
    }

    fun processGoodreadsResponse(goodreadsResponse: GoodreadsResponse, message: Message) {
        val results = goodreadsResponse.search.results.works
        if (!results.isNullOrEmpty() && results.size > 0) {
            val resultBook = results[0].bestGoodreadsBook
            val existingBook = bookService.findById(resultBook.id)
            when (existingBook) {
                null -> {
                    val book = Book(
                            title = resultBook.title,
                            smallImageUrl = resultBook.smallImageUrl,
                            imageUrl = resultBook.imageUrl,
                            message = setOf(message))
                    bookService.saveBook(book)
                }
                else -> {
                    val updated = existingBook.copy(message = existingBook.message.plus(message))
                    bookService.saveBook(updated)
                }
            }
        }
        toggleMessageProcessed(message)
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