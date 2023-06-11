package com.example.englishapp.presentation.fragments;

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

import com.example.englishapp.R;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.presentation.adapters.QuestionsAdapter;
import com.example.englishapp.data.database.DataBaseBookmarks;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.data.models.QuestionModel;
import com.example.englishapp.domain.repositories.BookmarkRepository;

import java.util.Objects;

public class BookmarksFragment extends Fragment {

    private static final String TAG = "FragmentBookmark";
    private RecyclerView bookmarkRecyclerView;
    private Dialog progressBar;
    private TextView questionNumber;
    private Button btnSave;
    private ImageView imgBookmark, imgBack, previousQuestion, nextQuestion;
    private int numberOfQuestion;
    private BookmarkRepository bookmarkRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        bookmarkRepository = new BookmarkRepository();

        init(view);

        setSnapHelper();

        setListeners();

        return view;
    }

    private void setListeners() {

        imgBack.setOnClickListener(v -> requireActivity().onBackPressed());

        previousQuestion.setOnClickListener(v -> {
            if (numberOfQuestion > 0) {
                bookmarkRecyclerView.smoothScrollToPosition(numberOfQuestion - 1);
            }
        });

        nextQuestion.setOnClickListener(v -> {
            if (numberOfQuestion < DataBaseBookmarks.LIST_OF_BOOKMARKS.size()) {
                bookmarkRecyclerView.smoothScrollToPosition(numberOfQuestion + 1);
            }
        });

        imgBookmark.setOnClickListener(v -> addToBookmark());

        btnSave.setOnClickListener(v -> saveBookmarks());

    }

    private void saveBookmarks() {

        progressBar.show();

        bookmarkRepository.saveBookmarks(new CompleteListener() {
            @Override
            public void OnSuccess() {
                progressBar.dismiss();

                Log.i(TAG, "successfully saved");

                Toast.makeText(getActivity(), "Bookmarks successfully saved", Toast.LENGTH_SHORT).show();

                ((MainActivity) requireActivity()).setFragment(new ProfileFragment());
            }

            @Override
            public void OnFailure() {
                progressBar.dismiss();

                Log.i(TAG, "error occurred");

                Toast.makeText(requireActivity(), "Can not save bookmarks", Toast.LENGTH_SHORT).show();

                ((MainActivity) requireActivity()).setFragment(new ProfileFragment());
            }
        });

    }

    private void init(View view) {

        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).hide();

        bookmarkRecyclerView = view.findViewById(R.id.bookmarksRecyclerView);
        questionNumber = view.findViewById(R.id.questionNumber);
        imgBookmark = view.findViewById(R.id.bookMarkImg);
        previousQuestion = view.findViewById(R.id.previousQuestion);
        nextQuestion = view.findViewById(R.id.nextQuestion);
        imgBack = view.findViewById(R.id.imgBack);
        btnSave = view.findViewById(R.id.btnSave);

        imgBookmark.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarSaving);

        progressBar.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        bookmarkRecyclerView.setLayoutManager(layoutManager);

        Log.i(TAG, "amount bookmarks - " + DataBaseBookmarks.LIST_OF_BOOKMARKS.size());

        QuestionsAdapter adapter = new QuestionsAdapter(DataBaseBookmarks.LIST_OF_BOOKMARKS,true, false);
        bookmarkRecyclerView.setAdapter(adapter);

        progressBar.dismiss();

        numberOfQuestion = 0;

        questionNumber.setText("1 / " + DataBaseBookmarks.LIST_OF_BOOKMARKS.size());


    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(bookmarkRecyclerView);

        bookmarkRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (view != null) {
                    numberOfQuestion = Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(view);
                }

                QuestionModel questionModel = DataBaseBookmarks.LIST_OF_BOOKMARKS.get(numberOfQuestion);

                // if question was bookmarked
                if (questionModel.isBookmarked()) {
                    imgBookmark.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    imgBookmark.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                }

                questionNumber.setText((numberOfQuestion + 1) + " / " + DataBaseBookmarks.LIST_OF_BOOKMARKS.size());
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void addToBookmark() {

        int status = bookmarkRepository.addToBookmark(numberOfQuestion);

        if (status == BookmarkRepository.CODE_BOOKMARKED) {
            imgBookmark.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            imgBookmark.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}
