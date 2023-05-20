package com.example.englishapp.testsAndWords;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.R;
public class BookmarksFragment extends Fragment {

    private RecyclerView bookmarkRecyclerView;
    private Toolbar toolbar;
    private Dialog progressBar;
    private TextView dialogText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        
        init(view);
        
        return view;
    }

    private void init(View view) {
        bookmarkRecyclerView = view.findViewById(R.id.bookmarksRecyclerView);

        progressBar = new Dialog(getContext());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText("Loading");

        progressBar.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        bookmarkRecyclerView.setLayoutManager(layoutManager);

        DataBase.loadBookmarks(new CompleteListener() {
            @Override
            public void OnSuccess() {
                QuestionsAdapter adapter = new QuestionsAdapter(DataBase.LIST_OF_BOOKMARKS, getContext(), true);
                bookmarkRecyclerView.setAdapter(adapter);

                progressBar.dismiss();
            }

            @Override
            public void OnFailure() {
                progressBar.dismiss();
            }
        });
    }
}