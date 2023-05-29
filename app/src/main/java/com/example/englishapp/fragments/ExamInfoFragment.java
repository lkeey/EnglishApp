package com.example.englishapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.englishapp.database.DataBase;
import com.example.englishapp.R;
import com.example.englishapp.models.QuestionModel;
import com.example.englishapp.activities.ExamActivity;
import com.example.englishapp.adapters.InfoQuestionsAdapter;
import com.example.englishapp.interfaces.QuestionListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ExamInfoFragment extends BottomSheetDialogFragment implements QuestionListener {

    private GridView gridQuestions;
    private InfoQuestionsAdapter infoQuestionsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exam_info, container, false);

        init(view);

        return view;
    }

    private void init(View view) {

        gridQuestions = view.findViewById(R.id.gridQuestions);

        infoQuestionsAdapter = new InfoQuestionsAdapter(DataBase.LIST_OF_QUESTIONS, ExamInfoFragment.this);
        gridQuestions.setAdapter(infoQuestionsAdapter);

    }

    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onQuestionClicked(QuestionModel questionModel, int position) {
        ExamActivity.goToQuestion(position);

        ExamInfoFragment.this.dismiss();
    }
}