package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
class GoodreadsBook {

    @Element(name = "id")
    var id: Long? = null

    @Element(name = "title")
    var title: String? = null

    @Element(name = "small_image_url")
    var smallImageUrl: String? = null

    @Element(name = "image_url")
    var imageUrl: String? = null

}
