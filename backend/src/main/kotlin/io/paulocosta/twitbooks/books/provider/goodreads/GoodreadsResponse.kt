package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Root(name = "GoodreadsResponse", strict = false)
@XmlAccessorType(XmlAccessType.FIELD)
class GoodreadsResponse {

    @Element(name = "search")
    var search: Search? = null
}
