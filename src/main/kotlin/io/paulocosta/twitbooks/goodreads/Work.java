package io.paulocosta.twitbooks.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "work", strict = false)
public class Work {

    public Work() {}

    @Element(name = "best_book")
    private Book bestBook;

    public Book getBestBook() {
        return bestBook;
    }

    public void setBestBook(Book bestBook) {
        this.bestBook = bestBook;
    }
}
