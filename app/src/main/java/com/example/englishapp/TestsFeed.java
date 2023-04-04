package com.example.englishapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class TestsFeed extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_TEST = 1;
    public static final int REQUEST_CODE_UPDATE_TEST = 2;
    public static final int REQUEST_CODE_SHOW_TEST = 3;

    private int testClickedPosition = -1;

    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_feed);
    }
}