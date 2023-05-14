package com.example.englishapp.testsAndWords;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.englishapp.chat.ChatFragment;

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
                Log.i(TAG, "TestsFragment");

                return new TestsFragment();

            default:
                Log.i(TAG, "WordsFragment");

                return new TestsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

