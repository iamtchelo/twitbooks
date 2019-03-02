package io.paulocosta.twitbooks.service

import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.process.CoreLabelTokenFactory
import edu.stanford.nlp.process.LexedTokenFactory
import edu.stanford.nlp.process.PTBTokenizer
import io.paulocosta.twitbooks.entity.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.StringReader
import java.util.*

@Service
class MessageTokenizerService @Autowired constructor(
        val messageService: MessageService) {

    fun parse(message: Message) {
        val tokenizer = PTBTokenizer(StringReader(message.text), createTokenizer(), "")
    }

    fun parse2(message: Message) {
        val pipeline = StanfordCoreNLP(getProperties())
        val document = CoreDocument(message.text)
        // TODO What does this do ?
        pipeline.annotate(document)
        val firstSentenceTokens = document.sentences()[0].tokens()
    }

    private fun getProperties(): Properties {
        val properties = Properties()
        properties.setProperty("annotators", "tokenize,ssplit")
        return properties
    }

    private fun createTokenizer(): LexedTokenFactory<CoreLabel> {
        return CoreLabelTokenFactory()
    }

}