package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.extensions.toNullable
import io.paulocosta.twitbooks.repository.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BookService @Autowired constructor(private val bookRepository: BookRepository) {

    fun saveBooks(books: List<Book>) {
        bookRepository.saveAll(books)
    }

    fun saveBook(book: Book) {
        bookRepository.save(book)
    }

    fun findById(bookId: Long): Book? {
        return bookRepository.findById(bookId).toNullable()
    }

}