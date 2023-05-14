package com.example.englishapp.testsAndWords;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.example.englishapp.MVP.FeedActivity;
import com.example.englishapp.R;

import java.util.ArrayList;
import java.util.List;

public class CreateTestFragment extends Fragment {
    private static final String TAG = "CreateTestFragment";
    private LinearLayout layoutList;
    private Button btnAdd, btnSubmit;
    private final ArrayList<QuestionModel> listOfQuestions = new ArrayList<>();
    private List<String> stringListOption = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_test, container, false);

        init(view);

        setListeners();

        return view;
    }

    private void init(View view) {
        ((FeedActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setTitle(R.string.nameCreateTest);
        ((FeedActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);

        layoutList = view.findViewById(R.id.layoutList);
        btnAdd = view.findViewById(R.id.btnAddQuestion);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        stringListOption.add("Wrong");
        stringListOption.add("Correct");
    }

    private void setListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (readData()) {
                // if data is valid

                Toast.makeText(getActivity(), "All okay", Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd.setOnClickListener(view -> addView());
    }

    private boolean readData() {

        boolean resultOption;

        listOfQuestions.clear();

        for(int i=0; i < layoutList.getChildCount(); i++) {
            resultOption = false;

            View viewChild = layoutList.getChildAt(i);

            EditText editText = viewChild.findViewById(R.id.editText);
            LinearLayout listOption = viewChild.findViewById(R.id.layoutListOptions);

            QuestionModel questionModel = new QuestionModel();

            if (!editText.getText().toString().isEmpty()) {
                questionModel.setQuestion(editText.getText().toString());
            } else {
                Toast.makeText(getActivity(), "Question must be not empty", Toast.LENGTH_SHORT).show();
                return false;
            }

            ArrayList<OptionModel> listOfOptions = new ArrayList<>();

            for(int n=0; n < listOption.getChildCount(); n++) {

                View viewOption = listOption.getChildAt(n);

                EditText editTextOption = viewOption.findViewById(R.id.editTextOption);
                AppCompatSpinner spinnerOption = viewOption.findViewById(R.id.spinnerOptions);

                OptionModel optionModel = new OptionModel();

                if (!editTextOption.getText().toString().isEmpty()) {
                    optionModel.setOption(editTextOption.getText().toString());
                } else {
                    Toast.makeText(getActivity(), "Option must be not empty", Toast.LENGTH_SHORT).show();

                    return false;
                }

                if (spinnerOption.getSelectedItemPosition() == 1) {

                    resultOption = true;
                }

                optionModel.setCorrect(Boolean.parseBoolean(stringListOption.get(spinnerOption.getSelectedItemPosition())));

                Log.i(TAG, "Option - " + optionModel.getOption() + " - " + optionModel.isCorrect());

                listOfOptions.add(optionModel);
            }

            if (!resultOption) {
                Toast.makeText(getActivity(), "There are must be at least 1 correct option", Toast.LENGTH_SHORT).show();

                return false;
            }

            if (listOfOptions.size() < 2) {
                Toast.makeText(getActivity(), "Must be at least 2 options", Toast.LENGTH_SHORT).show();

                return false;
            }

            questionModel.setOptionsList(listOfOptions);


            listOfQuestions.add(questionModel);
        }

        if(listOfQuestions.size() == 0) {

            Toast.makeText(getActivity(), "Add at least one question", Toast.LENGTH_SHORT).show();

            return false;

        }

        Toast.makeText(getActivity(), "Sending", Toast.LENGTH_SHORT).show();

        return true;
    }

    private void addView() {
        View view = getLayoutInflater().inflate(R.layout.row_add, null, false);

        ImageView imageView = view.findViewById(R.id.imageRemove);

        Button btnAddOption = view.findViewById(R.id.btnAddOption);

        imageView.setOnClickListener(v -> removeView(view));

        btnAddOption.setOnClickListener(v -> {
            LinearLayout layoutListOptions = view.findViewById(R.id.layoutListOptions);
            addViewOption(layoutListOptions);
        });

        layoutList.addView(view);
    }

    private void removeView(View view) {

        layoutList.removeView(view);

    }

    private void addViewOption(LinearLayout layoutListOptions) {
        View viewOption = getLayoutInflater().inflate(R.layout.row_option, null, false);

        AppCompatSpinner spinner = viewOption.findViewById(R.id.spinnerOptions);
        ImageView imageView = viewOption.findViewById(R.id.imageRemoveOption);


        ArrayAdapter adapter = new ArrayAdapter(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, stringListOption);
        spinner.setAdapter(adapter);

        imageView.setOnClickListener(v -> removeViewOption(layoutListOptions, viewOption));

        layoutListOptions.addView(viewOption);
    }

    private void removeViewOption(LinearLayout layoutList, View view) {
        layoutList.removeView(view);
    }

}