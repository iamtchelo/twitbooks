package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.extensions.toNullable
import io.paulocosta.twitbooks.repository.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService @Autowired constructor(private val bookRepository: BookRepository) {

    @Value("\${spring.profiles.active}")
    lateinit var activeProfile: String

    fun saveBook(book: Book) {
        bookRepository.save(book)
    }

    fun ignore(bookId: Long) {
        bookRepository.ignoreBook(bookId)
    }

    fun getAllBooks(pageable: Pageable): Page<Book> {
        return bookRepository.getAllBooks(pageable)
    }

    fun findById(bookId: Long): Book? {
        return bookRepository.findById(bookId).toNullable()
    }

    fun getBookCount(): Long {
        return bookRepository.count()
    }

    fun updateMessages(book: Book, messages: Set<Message>) {
        if (activeProfile != "prod") {
            bookRepository.save(book.copy(message = messages))
        }
    }

}