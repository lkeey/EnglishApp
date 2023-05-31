package com.example.englishapp.models;

public class Thumbnail {
    public String mimetype;
    public int size;
    public int width;
    public int height;
    public int duration;
    public String url;

    public Thumbnail(String mimetype, int size, int width, int height, int duration, String url) {
        this.mimetype = mimetype;
        this.size = size;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.url = url;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}