package com.example.englishapp.MVP;

import android.graphics.Bitmap;

import java.io.Serializable;

public class WordModel implements Serializable {

    private String id;
    private String description;
    private String level;
    private String textEn;
    private Bitmap image;

    public WordModel () {}

    public WordModel(String id, String description, String level, String textEn, Bitmap image) {
        this.id = id;
        this.description = description;
        this.level = level;
        this.textEn = textEn;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTextEn() {
        return textEn;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
