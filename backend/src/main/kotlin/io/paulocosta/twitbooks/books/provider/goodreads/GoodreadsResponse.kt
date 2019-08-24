package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Root(name = "GoodreadsResponse", strict = false)
@XmlAccessorType(XmlAccessType.FIELD)
data class GoodreadsResponse @JvmOverloads constructor(
        @field:Element(name = "search")
        @param:Element(name = "search")
        var search: Search? = null
)
