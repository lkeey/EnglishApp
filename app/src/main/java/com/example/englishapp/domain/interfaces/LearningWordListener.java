package com.example.englishapp.domain.interfaces;

public interface LearningWordListener {

    void beginLearning();

    void cancelLearning();

    void otherLearning();

    void onFail();

}
