package io.paulocosta.twitbooks.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "search", strict = false)
public class Search {

   public Search() {}

   @Element(name = "results")
   private Results results;

   public Results getResults() {
      return results;
   }

   public void setResults(Results results) {
      this.results = results;
   }
}
