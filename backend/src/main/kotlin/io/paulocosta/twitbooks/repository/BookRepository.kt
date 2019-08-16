package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import javax.transaction.Transactional

interface BookRepository : JpaRepository<Book, Long> {

    @Query("select * from books inner join book_user on books.id = book_user.book_id inner join users on users.id = book_user.user_id where users.twitter_id = ?1 and books.ignored = false", nativeQuery = true)
    fun getAllBooksByTwitterId(twitterId: String, pageable: Pageable): Page<Book>

    @Query("update Book book set book.ignored = '1' where book.id = :bookId")
    @Modifying
    @Transactional
    fun ignoreBook(bookId: Long)

}
