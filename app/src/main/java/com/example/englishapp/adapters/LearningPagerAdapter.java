package com.example.englishapp.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.englishapp.fragments.TestsFragment;
import com.example.englishapp.fragments.WordsFragment;

public class LearningPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "ChatViewPagerAdapter";

    public LearningPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                Log.i(TAG, "WordsFragment");

                return new WordsFragment();

            default:
                Log.i(TAG, "TestsFragment");

                return new TestsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

