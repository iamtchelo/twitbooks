package io.paulocosta.twitbooks.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Root(name = "GoodreadsResponse", strict = false)
@XmlAccessorType(XmlAccessType.FIELD)
public class GoodreadsResponse {

    public GoodreadsResponse() {}

    @Element(name = "search")
    private Search search;

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }
}
