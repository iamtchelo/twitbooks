package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.entity.BookAPIResponse
import io.paulocosta.twitbooks.service.BookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/books")
class BooksController @Autowired constructor(private val bookService: BookService) {

    @GetMapping
    fun getBooks(@RequestParam("page") page: Int?): Page<BookAPIResponse> {
        return bookService.getAllBooks(PageRequest.of(page ?: 0, 50))
                .map { BookAPIResponse(it.copy(message = emptySet()), it.message.map { message -> message.id }) }
    }

}