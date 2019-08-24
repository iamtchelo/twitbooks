package io.paulocosta.twitbooks.books.provider.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
data class GoodreadsBook @JvmOverloads constructor(

    @field:Element(name = "id")
    @param:Element(name = "id")
    var id: Long? = null,

    @field:Element(name = "title")
    @param:Element(name = "title")
    var title: String? = null,

    @field:Element(name = "small_image_url")
    @param:Element(name = "small_image_url")
    var smallImageUrl: String? = null,

    @field:Element(name = "image_url")
    @param:Element(name = "image_url")
    var imageUrl: String? = null

)
