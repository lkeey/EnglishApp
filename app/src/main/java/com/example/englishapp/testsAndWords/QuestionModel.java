package com.example.englishapp.testsAndWords;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionModel implements Serializable {

    private String question;
    private ArrayList<OptionModel> optionsList;

    public QuestionModel () {}

    public QuestionModel(String question, ArrayList<OptionModel> optionsList) {
        this.question = question;
        this.optionsList = optionsList;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<OptionModel> getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(ArrayList<OptionModel> optionsList) {
        this.optionsList = optionsList;
    }
}
