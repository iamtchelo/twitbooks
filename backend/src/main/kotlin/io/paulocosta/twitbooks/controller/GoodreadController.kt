package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.goodreads.GoodreadsResponse
import io.paulocosta.twitbooks.goodreads.GoodreadsService
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search")
class GoodreadController @Autowired constructor(val goodreadsService: GoodreadsService) {

    @GetMapping
    fun search(@RequestParam(name = "q") query: String): Single<GoodreadsResponse> {
        return goodreadsService.search(query)
    }

}