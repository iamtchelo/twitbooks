package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "work", strict = false)
data class Work @JvmOverloads constructor(
        @field:Element(name = "best_book")
        @param:Element(name = "best_book")
        var bestGoodreadsBook: GoodreadsBook? = null
)
