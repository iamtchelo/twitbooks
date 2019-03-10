package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookRepository : JpaRepository<Book, Long> {

    @Query("select book from Book book order by book.id")
    fun getAllBooks(pageable: Pageable): Page<Book>

}