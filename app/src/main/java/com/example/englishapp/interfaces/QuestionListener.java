package com.example.englishapp.interfaces;

import com.example.englishapp.models.QuestionModel;

public interface QuestionListener {
    void onQuestionClicked(QuestionModel questionModel, int position);

}
