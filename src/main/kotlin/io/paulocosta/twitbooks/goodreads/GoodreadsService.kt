package io.paulocosta.twitbooks.goodreads

import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GoodreadsService @Autowired constructor(val goodreadsSearch: GoodreadsSearch) {

    fun search(search: String): Single<GoodreadsResponse> {
        return goodreadsSearch.search(search)
    }

}