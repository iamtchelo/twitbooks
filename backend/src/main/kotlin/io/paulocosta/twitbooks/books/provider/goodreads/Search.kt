package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "search", strict = false)
data class Search @JvmOverloads constructor(
        @field:Element(name = "results")
        @param:Element(name = "results")
        var results: Results? = null
)
