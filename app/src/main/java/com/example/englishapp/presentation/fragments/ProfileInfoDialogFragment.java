package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.KEY_ADD_SCORE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfileInfoDialogFragment extends BottomSheetDialogFragment {

    private TextView textClose;
    private Button btnAgree;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info_dialog, container, false);

        init(view);

        setListeners();

        return view;
    }

    private void init(View view) {
        textClose = view.findViewById(R.id.textClose);
        btnAgree = view.findViewById(R.id.textAgree);

    }

    private void setListeners() {
        textClose.setOnClickListener(v -> this.dismiss());

        btnAgree.setOnClickListener(v -> (
                (MainActivity) requireActivity()).setFragment(new ProfileInfoFragment())
        );

        btnAgree.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putBoolean(KEY_ADD_SCORE, true);
            ProfileInfoFragment fragment = new ProfileInfoFragment();
            fragment.setArguments(bundle);

            ((MainActivity) requireActivity()).setFragment(fragment);
            ProfileInfoDialogFragment.this.dismiss();
        });
    }


    @Override
    public int getTheme() {
        // to set border radius

        return R.style.AppBottomSheetDialogTheme;
    }
}