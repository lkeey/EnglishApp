package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_TEST;
import static com.example.englishapp.data.database.DataBaseExam.NOT_VISITED;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.englishapp.R;
import com.example.englishapp.data.database.DataBaseQuestions;
import com.example.englishapp.data.models.TestModel;
import com.example.englishapp.presentation.activities.ExamActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TestInfoDialogFragment extends BottomSheetDialogFragment {

    private TextView nameTest, amountQuestions, bestScore, amountTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_info_dialog, container, false);

        init(view);

        receiveData();

        return view;
    }

    private void init(View view) {

        TextView textClose = view.findViewById(R.id.textClose);
        nameTest = view.findViewById(R.id.nameTest);
        amountQuestions = view.findViewById(R.id.amountQuestions);
        bestScore = view.findViewById(R.id.bestScore);
        amountTime = view.findViewById(R.id.amountTime);
        Button btnDoTest = view.findViewById(R.id.btnDoTest);

        textClose.setOnClickListener(v -> TestInfoDialogFragment.this.dismiss());

        btnDoTest.setOnClickListener(v -> {

            DataBaseQuestions.LIST_OF_QUESTIONS.size();
            Intent intent = new Intent(requireActivity(), ExamActivity.class);
            startActivity(intent);

            TestInfoDialogFragment.this.dismiss();

        });

        // set all answers with basic data
        for (int i = 0; i < DataBaseQuestions.LIST_OF_QUESTIONS.size(); i++) {
            DataBaseQuestions.LIST_OF_QUESTIONS.get(i).setSelectedOption(-1);
            DataBaseQuestions.LIST_OF_QUESTIONS.get(i).setStatus(NOT_VISITED);
        }

    }


    private void receiveData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            TestModel receivedTest = (TestModel) bundle.getSerializable(KEY_CHOSEN_TEST);

            nameTest.setText(receivedTest.getName());
            amountQuestions.setText(String.valueOf(receivedTest.getAmountOfQuestion()));
            bestScore.setText(String.valueOf(receivedTest.getTopScore()));
            amountTime.setText(String.valueOf(receivedTest.getTime()));

        }
    }

    @Override
    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}
