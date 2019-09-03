package io.paulocosta.twitbooks.books.provider.google

data class GoogleBooksApiResponse(
        val totalItems: Int,
        val items: List<Item>
)
