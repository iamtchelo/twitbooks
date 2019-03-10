package io.paulocosta.twitbooks.goodreads;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class GoodreadsBook {

    public GoodreadsBook() {}

    @Element(name = "id")
    private Long id;

    @Element(name = "title")
    private String title;

    @Element(name = "small_image_url")
    private String smallImageUrl;

    @Element(name = "image_url")
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
