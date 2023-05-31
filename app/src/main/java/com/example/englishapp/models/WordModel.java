package com.example.englishapp.models;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "words")
public class WordModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "level")
    private String level;

    @ColumnInfo(name = "textEn")
    private String textEn;

    @ColumnInfo(name = "image")
    private String image;

    public WordModel () {}

    public WordModel(int id, String description, String level, String textEn, Bitmap bitmap) {
        this.id = id;
        this.description = description;
        this.level = level;
        this.textEn = textEn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
