package com.example.englishapp.presentation.adapters;

import static com.example.englishapp.data.database.DataBaseExam.ANSWERED;
import static com.example.englishapp.data.database.DataBaseExam.REVIEW;
import static com.example.englishapp.data.database.DataBaseExam.UNANSWERED;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.englishapp.R;
import com.example.englishapp.domain.interfaces.QuestionListener;
import com.example.englishapp.data.models.QuestionModel;

import java.util.List;

public class InfoQuestionsAdapter extends BaseAdapter {

    private static final String TAG = "InfoQuestionsAdapter";
    private final List<QuestionModel> listOfQuestions;
    private final QuestionListener listener;

    public InfoQuestionsAdapter(List<QuestionModel> listOfQuestions, QuestionListener listener) {
        this.listOfQuestions = listOfQuestions;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return listOfQuestions.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfQuestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        View myView;

        QuestionModel questionModel = listOfQuestions.get(position);

        Log.i(TAG, "model - " + questionModel.getQuestion() + " - " + questionModel.getStatus());

        if (view == null) {
            myView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_grid_item, viewGroup, false);

        } else {
            myView = view;
        }

        myView.setOnClickListener(v -> listener.onQuestionClicked(questionModel, position));

        TextView questionNumber = myView.findViewById(R.id.questionNumber);
        questionNumber.setText("" + (position + 1));

        switch (questionModel.getStatus()) {
            case UNANSWERED ->
                    questionNumber.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(), R.color.red)));
            case ANSWERED ->
                    questionNumber.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(), com.instabug.bug.R.color.design_default_color_primary)));
            case REVIEW ->
                    questionNumber.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(), R.color.secondary_color)));
            default ->
                    questionNumber.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(), R.color.grey)));
        }

        return myView;
    }
}
