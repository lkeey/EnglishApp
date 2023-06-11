package com.example.englishapp.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.englishapp.R;
import com.example.englishapp.presentation.activities.ExamActivity;
import com.example.englishapp.presentation.adapters.InfoQuestionsAdapter;
import com.example.englishapp.data.database.DataBaseQuestions;
import com.example.englishapp.domain.interfaces.QuestionListener;
import com.example.englishapp.data.models.QuestionModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ExamInfoFragment extends BottomSheetDialogFragment implements QuestionListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exam_info, container, false);

        init(view);

        return view;
    }

    private void init(View view) {

        GridView gridQuestions = view.findViewById(R.id.gridQuestions);

        InfoQuestionsAdapter infoQuestionsAdapter = new InfoQuestionsAdapter(DataBaseQuestions.LIST_OF_QUESTIONS, ExamInfoFragment.this);
        gridQuestions.setAdapter(infoQuestionsAdapter);

    }

    @Override
    public void onQuestionClicked(QuestionModel questionModel, int position) {
        ExamActivity.goToQuestion(position);

        ExamInfoFragment.this.dismiss();
    }

    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}