package com.example.englishapp.models;

import com.google.gson.annotations.SerializedName;

public class ImageGoogle {

    @SerializedName("thumbnailLink")
    private String url;

    public ImageGoogle(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
