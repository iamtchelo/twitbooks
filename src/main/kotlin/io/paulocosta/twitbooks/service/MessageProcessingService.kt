package io.paulocosta.twitbooks.service

import io.reactivex.Observable
import opennlp.tools.langdetect.LanguageDetectorFactory
import opennlp.tools.ngram.NGramUtils
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.tokenize.Tokenizer
import opennlp.tools.util.normalizer.TwitterCharSequenceNormalizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MessageProcessingService @Autowired constructor(
        val messageService: MessageService,
        val userService: UserService) {

    @Value("\${clear.data:false}")
    var clearData: Boolean = false

    fun process() {
        val users = userService.getAllUsers()
        users.forEach { friend ->
            val page = messageService.getAllMessages(friend.id, PageRequest.of(1, 100))
            val messages = page.content
            messages.forEach { message ->
                //val nGrams = customLanguageDetector.contextGenerator.getContext(it.text)
                //nGrams
                val normalizedText = normalizeText(message.text)
                val tokenizer = SimpleTokenizer.INSTANCE
                val tokens = tokenizer.tokenize(normalizedText).filter { it.first().isUpperCase() }.toTypedArray()

//                val bigrams = NGramUtils.getNGrams(tokens, 2)
//                val trigrams = NGramUtils.getNGrams(tokens, 3)
//                trigrams
            }
        }
    }

//    fun getNgrams(text: String): Observable<String> {
//    }

    fun normalizeText(text: String): String {
        return TwitterCharSequenceNormalizer.getInstance().normalize(text).toString().trim()
    }

}