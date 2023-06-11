package com.example.englishapp.data.models;

public class Page {
    public int id;
    public String key;
    public String title;
    public String excerpt;
    public String matched_title;
    public String description;
    public Thumbnail thumbnail;

    public Page(int id, String key, String title, String excerpt, String matched_title, String description, Thumbnail thumbnail) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.excerpt = excerpt;
        this.matched_title = matched_title;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getMatched_title() {
        return matched_title;
    }

    public void setMatched_title(String matched_title) {
        this.matched_title = matched_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}
