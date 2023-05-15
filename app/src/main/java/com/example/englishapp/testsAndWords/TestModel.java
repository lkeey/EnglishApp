package com.example.englishapp.testsAndWords;

import java.io.Serializable;

public class TestModel implements Serializable {
    private String id;
    private String name;
    private int amountOfQuestion;
    private int topScore;
    private int time;

    public TestModel () {}

    public TestModel(String id, String name, int amountOfQuestion, int topScore, int time) {
        this.id = id;
        this.name = name;
        this.amountOfQuestion = amountOfQuestion;
        this.topScore = topScore;
        this.time = time;
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

    public int getAmountOfQuestion() {
        return amountOfQuestion;
    }

    public void setAmountOfQuestion(int amountOfQuestion) {
        this.amountOfQuestion = amountOfQuestion;
    }

    public int getTopScore() {
        return topScore;
    }

    public void setTopScore(int topScore) {
        this.topScore = topScore;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
