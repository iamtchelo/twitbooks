package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long>