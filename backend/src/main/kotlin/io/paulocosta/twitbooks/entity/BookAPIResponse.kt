package io.paulocosta.twitbooks.entity

data class BookAPIResponse(
        val book: Book,
        val messageIds: List<Long>
)