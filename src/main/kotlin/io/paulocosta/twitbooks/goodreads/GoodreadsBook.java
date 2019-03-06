package io.paulocosta.twitbooks.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class GoodreadsBook {

    public GoodreadsBook() {}

    @Element(name = "title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
