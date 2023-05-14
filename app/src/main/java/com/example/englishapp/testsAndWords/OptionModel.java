package com.example.englishapp.testsAndWords;

import java.io.Serializable;

public class OptionModel implements Serializable {

    private String option;
    private boolean isCorrect;

    public OptionModel () {}

    public OptionModel(String option, boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
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
}
