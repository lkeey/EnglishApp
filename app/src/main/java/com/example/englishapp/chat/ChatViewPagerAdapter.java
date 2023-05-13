package com.example.englishapp.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ChatViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "ChatViewPagerAdapter";

    public ChatViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                Log.i(TAG, "MapUsersFragment");

                return new MapUsersFragment();
            default:
                Log.i(TAG, "ChatFragment");

                return new ChatFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
