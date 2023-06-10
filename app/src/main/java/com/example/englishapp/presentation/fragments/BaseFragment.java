package com.example.englishapp.presentation.fragments;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.englishapp.interfaces.RefreshListener;
import com.example.englishapp.presentation.activities.MainActivity;

public class BaseFragment extends Fragment implements RefreshListener {

    private static final String TAG = "FragmentBase";
    private MainActivity mainActivity = null;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
            Log.i(TAG, "context");
        } else {
            Log.i(TAG, "null context");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mainActivity != null) {
            mainActivity.addListener(this);
            Log.i(TAG, "added " + this);
        } else{
            Log.i(TAG, "null activity");
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        if (mainActivity != null) {
            mainActivity.removeListener(this);
            Log.i(TAG, "removed - " + this);
        } else {
            Log.i(TAG, "null activity");
        }

    }

    @Override
    public void onRefresh() {}
}
