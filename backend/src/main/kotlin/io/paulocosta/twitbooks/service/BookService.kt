package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.SecurityHelper
import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.extensions.toNullable
import io.paulocosta.twitbooks.repository.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService @Autowired constructor(private val bookRepository: BookRepository) {

    fun saveBook(book: Book) {
        bookRepository.save(book)
    }

    fun ignore(bookId: Long) {
        bookRepository.ignoreBook(bookId)
    }

    fun getAllBooks(pageable: Pageable): Page<Book> {
        val twitterId = SecurityHelper.getTwitterId()
        return bookRepository.getAllBooksByTwitterId(twitterId, pageable)
    }

    fun findById(bookId: Long): Book? {
        return bookRepository.findById(bookId).toNullable()
    }

    fun getBookCount(): Long {
        return bookRepository.count()
    }

    fun updateBook(book: Book, messages: Set<Message>, users: Set<User>) {
        bookRepository.save(book.copy(
                message = messages,
                users = users))
    }

}