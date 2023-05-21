package com.example.englishapp.testsAndWords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.englishapp.R;

public class CreateWordCardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_word_card, container, false);
        
        init(view);
        
        return view;
    }

    private void init(View view) {
    }
}