package io.paulocosta.twitbooks.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "work", strict = false)
public class Work {

    public Work() {}

    @Element(name = "best_book")
    private GoodreadsBook bestGoodreadsBook;

    public GoodreadsBook getBestGoodreadsBook() {
        return bestGoodreadsBook;
    }

    public void setBestGoodreadsBook(GoodreadsBook bestGoodreadsBook) {
        this.bestGoodreadsBook = bestGoodreadsBook;
    }
}
