package com.example.englishapp.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.R;
import com.example.englishapp.models.QuestionModel;
import com.example.englishapp.adapters.QuestionsAdapter;

import java.util.Iterator;

public class BookmarksFragment extends Fragment {

    private static final String TAG = "FragmentBookmark";
    private RecyclerView bookmarkRecyclerView;
    private Dialog progressBar;
    private TextView dialogText, questionNumber;
    private Button btnSave;
    private ImageView imgBookmark, imgBack, previousQuestion, nextQuestion;
    private int numberOfQuestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        init(view);

        setSnapHelper();

        setListeners();

        return view;
    }

    private void setListeners() {

        imgBack.setOnClickListener(v -> getActivity().onBackPressed());

        previousQuestion.setOnClickListener(v -> {
            if (numberOfQuestion > 0) {
                bookmarkRecyclerView.smoothScrollToPosition(numberOfQuestion - 1);
            }
        });

        nextQuestion.setOnClickListener(v -> {
            if (numberOfQuestion < DataBase.LIST_OF_BOOKMARKS.size()) {
                bookmarkRecyclerView.smoothScrollToPosition(numberOfQuestion + 1);
            }
        });

        imgBookmark.setOnClickListener(v -> addToBookmark());

        btnSave.setOnClickListener(v -> saveBookmarks());

    }

    private void saveBookmarks() {

        Iterator<QuestionModel> questionModelIterator = DataBase.LIST_OF_BOOKMARKS.iterator();

        while(questionModelIterator.hasNext()) {

            QuestionModel nextQuestion = questionModelIterator.next();
            if (!nextQuestion.isBookmarked()) {
                questionModelIterator.remove();

                Log.i(TAG, "removed - " + nextQuestion.getQuestion());
            }
        }

        DataBase.USER_MODEL.setBookmarksCount(DataBase.LIST_OF_BOOKMARKS.size());

        progressBar.show();

        DataBase.saveBookmarks(new CompleteListener() {
            @Override
            public void OnSuccess() {

                progressBar.dismiss();

                Log.i(TAG, "successfully saved");

                Toast.makeText(getActivity(), "Bookmarks successfully saved", Toast.LENGTH_SHORT).show();

                ((MainActivity) getActivity()).setFragment(new ProfileFragment());
            }

            @Override
            public void OnFailure() {
                progressBar.dismiss();

                Log.i(TAG, "error occurred");

                Toast.makeText(getActivity(), "Can not save bookmarks", Toast.LENGTH_SHORT).show();

                ((MainActivity) getActivity()).setFragment(new ProfileFragment());
            }
        });

    }

    private void init(View view) {

        ((MainActivity) getActivity()).getSupportActionBar().hide();

        bookmarkRecyclerView = view.findViewById(R.id.bookmarksRecyclerView);
        questionNumber = view.findViewById(R.id.questionNumber);
        imgBookmark = view.findViewById(R.id.bookMarkImg);
        previousQuestion = view.findViewById(R.id.previousQuestion);
        nextQuestion = view.findViewById(R.id.nextQuestion);
        imgBack = view.findViewById(R.id.imgBack);
        btnSave = view.findViewById(R.id.btnSave);

        imgBookmark.setColorFilter(ContextCompat.getColor(getActivity(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText("Loading");

        progressBar.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        bookmarkRecyclerView.setLayoutManager(layoutManager);

        Log.i(TAG, "amount bookmarks - " + DataBase.LIST_OF_BOOKMARKS.size());

        QuestionsAdapter adapter = new QuestionsAdapter(DataBase.LIST_OF_BOOKMARKS, getActivity(), true);
        bookmarkRecyclerView.setAdapter(adapter);

        progressBar.dismiss();

        numberOfQuestion = 0;

        questionNumber.setText("1 / " + DataBase.LIST_OF_BOOKMARKS.size());


    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(bookmarkRecyclerView);

        bookmarkRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                numberOfQuestion = recyclerView.getLayoutManager().getPosition(view);

                QuestionModel questionModel = DataBase.LIST_OF_BOOKMARKS.get(numberOfQuestion);

                // if question was bookmarked
                if (questionModel.isBookmarked()) {
                    imgBookmark.setColorFilter(ContextCompat.getColor(getActivity(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    imgBookmark.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                }

                questionNumber.setText((numberOfQuestion + 1) + "/" + DataBase.LIST_OF_BOOKMARKS.size());
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void addToBookmark() {

        QuestionModel questionModel = DataBase.LIST_OF_BOOKMARKS.get(numberOfQuestion);

        if (questionModel.isBookmarked()) {

            Log.i(TAG, "Already bookmark");

            DataBase.LIST_OF_BOOKMARKS.get(numberOfQuestion).setBookmarked(false);

            imgBookmark.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        } else {

            Log.i(TAG, "New bookmark");

            DataBase.LIST_OF_BOOKMARKS.get(numberOfQuestion).setBookmarked(true);

            imgBookmark.setColorFilter(ContextCompat.getColor(getActivity(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}