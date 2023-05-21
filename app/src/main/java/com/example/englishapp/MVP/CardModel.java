package com.example.englishapp.MVP;

import java.io.Serializable;
import java.util.List;

public class CardModel implements Serializable {

    private String id;
    private String name;
    private String level;
    private int amountOfWords;
    private List<WordModel> words;

    public CardModel () {}

    public CardModel(String id, String name, String level, int amountOfWords, List<WordModel> words) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.amountOfWords = amountOfWords;
        this.words = words;
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

    public List<WordModel> getWords() {
        return words;
    }

    public void setWords(List<WordModel> words) {
        this.words = words;
    }
}
