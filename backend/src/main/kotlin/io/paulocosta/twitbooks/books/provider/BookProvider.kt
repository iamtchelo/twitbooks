package io.paulocosta.twitbooks.books.provider

import javax.persistence.Entity
import javax.persistence.Id

enum class BookProviderKey {
    GOODREADS, GOOGLE_BOOKS, AMAZON
}

@Entity
data class BookProvider(
        @Id
        val id: Long,
        val key: BookProviderKey
)
