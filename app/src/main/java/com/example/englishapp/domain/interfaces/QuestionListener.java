package com.example.englishapp.domain.interfaces;

import com.example.englishapp.data.models.QuestionModel;

public interface QuestionListener {
    void onQuestionClicked(QuestionModel questionModel, int position);

}
