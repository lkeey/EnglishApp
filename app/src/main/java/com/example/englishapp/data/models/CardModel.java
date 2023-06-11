package com.example.englishapp.data.models;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class CardModel implements Serializable {

    private String id;
    private String name;
    private String level;
    private String description;
    private String author;
    private int amountOfWords;

    public CardModel () {}

    public CardModel(String id, String name, String level, String description, String author, int amountOfWords) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.description = description;
        this.author = author;
        this.amountOfWords = amountOfWords;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getAmountOfWords() {
        return amountOfWords;
    }

    public void setAmountOfWords(int amountOfWords) {
        this.amountOfWords = amountOfWords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean equals(@Nullable String str) {
        return this.level.equals(str);
    }
}
