package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import javax.transaction.Transactional

interface BookRepository : JpaRepository<Book, Long> {

    @Query("select book from Book book where book.ignored = '0' order by book.createdDate desc")
    fun getAllBooks(pageable: Pageable): Page<Book>

    @Query("update Book book set book.ignored = '1' where book.id = :bookId")
    @Modifying
    @Transactional
    fun ignoreBook(bookId: Long)

}