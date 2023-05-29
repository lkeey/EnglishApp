package com.example.englishapp.models;

import java.util.List;

public class ComplexTest {
    public List<ModelTest> testsList;

    public ComplexTest(List<ModelTest> testsList) {
        this.testsList = testsList;
    }
}

class ModelTest {
    public String question;
    public List<Option> options;

    public ModelTest(String question, List<Option> options) {
        this.question = question;
        this.options = options;
    }
}

class Option {
    public String text;
    public Boolean checked;

    public Option(String text, Boolean checked) {
        this.text = text;
        this.checked = checked;
    }
}
