package io.paulocosta.twitbooks.books.provider

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

enum class ProviderKey {
    GOODREADS, GOOGLE_BOOKS, AMAZON
}

@Entity
@Table(name = "providers")
data class Provider(
        @Id
        val id: Long,
        val key: ProviderKey
) {
    companion object {
        val GOODREADS = Provider(1, ProviderKey.GOODREADS)
        val AMAZON = Provider(2, ProviderKey.AMAZON)
        val GOOGLE_BOOKS = Provider(3, ProviderKey.GOOGLE_BOOKS)
    }
}
