package com.example.englishapp.data.models;

import android.widget.TextView;

import java.io.Serializable;

public class OptionModel implements Serializable {

    private String option;
    private boolean isCorrect;
    private TextView tv;

    public OptionModel () {}

    public OptionModel(String option, boolean isCorrect, TextView tv) {
        this.option = option;
        this.isCorrect = isCorrect;
        this.tv = tv;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }
}
