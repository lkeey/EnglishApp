package com.example.englishapp.testsAndWords;

public class TestModel {
    private String id;
    private String name;
    private int topScore;
    private int time;

    public TestModel () {}

    public TestModel(String id, String name, int topScore, int time) {
        this.id = id;
        this.name = name;
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
