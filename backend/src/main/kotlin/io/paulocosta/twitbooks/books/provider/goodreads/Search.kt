package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "search", strict = false)
class Search {

   @Element(name = "results")
   var results: Results? = null

}
