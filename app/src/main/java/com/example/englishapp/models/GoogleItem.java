package com.example.englishapp.models;

import com.google.gson.annotations.SerializedName;

public class GoogleItem {

    @SerializedName("image")
    public ImageGoogle image;

    public GoogleItem(ImageGoogle image) {
        this.image = image;
    }

    public ImageGoogle getImage() {
        return image;
    }

    public void setImage(ImageGoogle image) {
        this.image = image;
    }
}
