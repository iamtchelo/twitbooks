package io.paulocosta.twitbooks.goodreads;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "results", strict = false)
public class Results {

    public Results() {}

    @ElementList(name = "work", inline = true)
    private List<Work> works;

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }
}
