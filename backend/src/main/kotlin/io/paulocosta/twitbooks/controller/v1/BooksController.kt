package io.paulocosta.twitbooks.controller.v1

import io.paulocosta.twitbooks.books.BookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

data class BookApiResponse(
        val id: Long,
        val title: String,
        val imageUrl: String,
        val detailsUrl: String
)

@RestController
@RequestMapping("/api/v1/books")
class BooksController @Autowired constructor(private val bookService: BookService) {

    @GetMapping
    fun getBooks(@RequestParam("page") page: Int?): Page<BookApiResponse> {
        return bookService.getAllBooks(PageRequest.of(page ?: 0, 50))
                .map {
                    BookApiResponse(it.id, it.title, it.imageUrl, it.detailsUrl)
                }
    }

    @PutMapping
    fun ignoreBook(@RequestParam("book_id") bookId: Long) {
        bookService.ignore(bookId)
    }

}
