package io.paulocosta.twitbooks.ner

import io.reactivex.Single

interface NERService {

    fun detectEntities(text: String): Single<List<String>>

}
