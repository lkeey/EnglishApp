package com.example.englishapp.testsAndWords;

import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_TEST;
import static com.example.englishapp.messaging.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.MainActivity;
import com.example.englishapp.R;

public class TestsFragment extends Fragment implements TestClickedListener {

    private static final String TAG = "TestsFragment";
    private RecyclerView testRecycler;
    private ImageView imgAddTest;
    private EditText inputSearch;
    private ProgressBar progressCategory;
    private Dialog progressBar;
    private TextView dialogText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tests, container, false);

        init(view);

        setListeners();

        return view;
    }

    private void setListeners() {
        imgAddTest.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
//            bundle.putSerializable(KEY_CHOSEN_CATEGORY, category);
            CreateTestFragment fragment = new CreateTestFragment();
            fragment.setArguments(bundle);

            ((MainActivity) getActivity()).setFragment(fragment);
        });
    }

    private void init(View view) {
        ((MainActivity) getActivity()).setTitle(R.string.nameTests);

        testRecycler = view.findViewById(R.id.testRecyclerView);
        imgAddTest = view.findViewById(R.id.imgAddTest);
        inputSearch = view.findViewById(R.id.inputSearch);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogText = progressBar.findViewById(R.id.dialogText);

        dialogText.setText(R.string.progressBarOpening);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        testRecycler.setLayoutManager(manager);

        progressBar.show();


        DataBase.loadTestsData(new CompleteListener() {
            @Override
            public void OnSuccess() {

                TestAdapter testAdapter = new TestAdapter(DataBase.LIST_OF_TESTS, TestsFragment.this, getContext());
                testRecycler.setAdapter(testAdapter);

                Log.i(TAG, "Successfully loaded");
                progressBar.dismiss();

            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not load tests");
                progressBar.dismiss();
            }
        });
    }

    @Override
    public void onTestClicked(TestModel test) {

        DataBase.CHOSEN_TEST_ID = test.getId();

        TestInfoDialogFragment fragment = new TestInfoDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CHOSEN_TEST, test);
        fragment.setArguments(bundle);

        DataBase.loadQuestions(new CompleteListener() {
            @Override
            public void OnSuccess() {

                Log.i(TAG, "Questions loaded");

                fragment.show(getParentFragmentManager(), SHOW_FRAGMENT_DIALOG);

            }

            @Override
            public void OnFailure() {
                Log.i(TAG, "Can not load questions");

                Toast.makeText(getActivity(), "Can not load test... Try later", Toast.LENGTH_SHORT).show();

            }
        });



    }
}