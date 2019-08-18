package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "results", strict = false)
class Results {

    @ElementList(name = "work", inline = true, required = false)
    var works: List<Work> = emptyList()

}
