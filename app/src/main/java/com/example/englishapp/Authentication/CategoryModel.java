package com.example.englishapp.Authentication;

public class CategoryModel {
    private String name;
    private String id;
    private int numberOfTests;

    public CategoryModel () {}

    public CategoryModel(String name, String id, int numberOfTests) {
        this.name = name;
        this.id = id;
        this.numberOfTests = numberOfTests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumberOfTests() {
        return numberOfTests;
    }

    public void setNumberOfTests(int numberOfTests) {
        this.numberOfTests = numberOfTests;
    }
}
