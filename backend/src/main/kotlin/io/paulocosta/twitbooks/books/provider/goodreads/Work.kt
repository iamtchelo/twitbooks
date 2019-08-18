package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "work", strict = false)
class Work {

    @Element(name = "best_book")
    var bestGoodreadsBook: GoodreadsBook? = null

}
