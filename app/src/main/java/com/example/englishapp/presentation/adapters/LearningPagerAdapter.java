package com.example.englishapp.presentation.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.englishapp.presentation.fragments.WordsFragment;
import com.example.englishapp.domain.interfaces.RefreshListener;
import com.example.englishapp.presentation.fragments.TestsFragment;

import java.util.ArrayList;
import java.util.List;

public class LearningPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments = new ArrayList<>();

    public LearningPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        fragments.add(new TestsFragment());
        fragments.add(new WordsFragment());
    }

    public void refreshFragment(int position) {
        ((RefreshListener) fragments.get(position)).onRefresh();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}

