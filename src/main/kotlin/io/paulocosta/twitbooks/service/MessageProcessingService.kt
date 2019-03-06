package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.goodreads.GoodreadsResponse
import io.paulocosta.twitbooks.goodreads.GoodreadsService
import io.reactivex.Observable
import mu.KotlinLogging
import opennlp.tools.langdetect.LanguageDetectorFactory
import opennlp.tools.ngram.NGramUtils
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.tokenize.Tokenizer
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
        val goodreadsService: GoodreadsService) {

    @Value("\${clear.data:false}")
    var clearData: Boolean = false

    fun process() {
        val users = userService.getAllUsers()
        users.forEach { friend ->
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
                        .debounce(1, TimeUnit.SECONDS)
                        .flatMapSingle { goodreadsService.search(it) }
                        .subscribe({ processGoodreadsResponse(it) }, {logger.error { it }})
            }
        }
    }

    fun processGoodreadsResponse(goodreadsResponse: GoodreadsResponse) {
        // TODO process message
    }

    fun getNgrams(tokens: Array<String>): Observable<String> {
        val nGramStreams = arrayOf(1..6).map { i ->
            val ngrams = NGramUtils.getNGrams(tokens, i.step)
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