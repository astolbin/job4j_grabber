package ru.job4j.html;

import java.time.LocalDateTime;

public class Post {
    private String name;
    private String text;
    private String link;
    private LocalDateTime create;

    public Post(String name, String text, String link, LocalDateTime create) {
        this.name = name;
        this.text = text;
        this.link = link;
        this.create = create;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getCreate() {
        return create;
    }

    public void setCreate(LocalDateTime create) {
        this.create = create;
    }

    @Override
    public String toString() {
        return "Post{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", link='" + link + '\'' +
                ", create=" + create +
                '}';
    }
}
