package com.example.englishapp.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionModel implements Serializable {

    private String id;
    private String question;
    private ArrayList<OptionModel> optionsList;
    private int correctAnswer;
    private int selectedOption;
    private int status;
    private boolean isBookmarked;
    private Bitmap bmp;

    public QuestionModel () {}

    public QuestionModel(String id, String question, ArrayList<OptionModel> optionsList, int correctAnswer, int selectedOption, int status, boolean isBookmarked, Bitmap bmp) {
        this.id = id;
        this.question = question;
        this.optionsList = optionsList;
        this.correctAnswer = correctAnswer;
        this.selectedOption = selectedOption;
        this.status = status;
        this.isBookmarked = isBookmarked;
        this.bmp = bmp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public boolean equals(QuestionModel obj) {
        return this.id.equals(obj.getId());
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }
}
