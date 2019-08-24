package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "results", strict = false)
data class Results @JvmOverloads constructor(
        @field:ElementList(name = "work", inline = true, required = false)
        @param:ElementList(name = "work", inline = true, required = false)
        var works: List<Work> = emptyList()
)
