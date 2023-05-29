package com.example.englishapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.englishapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class CustomBottomSheetDialog extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_bottom_sheet_dialog, container, false);

    }

}