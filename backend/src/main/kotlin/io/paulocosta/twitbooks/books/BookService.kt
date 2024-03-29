package io.paulocosta.twitbooks.books

import io.paulocosta.twitbooks.entity.Book
import io.paulocosta.twitbooks.entity.Message
import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.extensions.toNullable
import io.paulocosta.twitbooks.repository.BookRepository
import io.reactivex.Single
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

    fun getAllBooks(id: String, pageable: Pageable): Page<Book> {
        return bookRepository.getAllBooksByTwitterId(id, pageable)
    }

    fun getAllBooksCount(userId: String): Single<Long> {
        return Single.just(bookRepository.getBookCountByTwitterId(userId))
    }

    fun findById(bookId: Long): Book? {
        return bookRepository.findById(bookId).toNullable()
    }

    fun getBookCount(): Single<Long> {
        return Single.just(bookRepository.count())
    }

    fun updateBook(book: Book, messages: Set<Message>, users: Set<User>) {
        bookRepository.save(book.copy(
                message = messages,
                users = users))
    }

    fun findByProvider(key: String, providerId: Long): Book? {
        return bookRepository.findByProvider(key, providerId).firstOrNull()
    }

}
