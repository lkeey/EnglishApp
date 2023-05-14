package com.example.englishapp.testsAndWords;

import static com.example.englishapp.MVP.DataBase.CHOSEN_CATEGORY_ID;

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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.FeedActivity;
import com.example.englishapp.R;

public class TestsFragment extends Fragment {

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

            ((FeedActivity) getActivity()).setFragment(fragment);
        });
    }

    private void init(View view) {
        ((FeedActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setTitle(CHOSEN_CATEGORY_ID);
        ((FeedActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((FeedActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);

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
}