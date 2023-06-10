package com.example.englishapp.presentation.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager2.widget.ViewPager2;

import com.example.englishapp.R;
import com.example.englishapp.presentation.adapters.LearningPagerAdapter;
import com.example.englishapp.presentation.fragments.BaseFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class SplashLearningFragment extends BaseFragment {

    private static final String TAG = "SplashLearningFragment";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private LearningPagerAdapter viewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_splash_learning, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        viewAdapter = new LearningPagerAdapter(requireActivity());
        viewPager.setAdapter(viewAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabSelected - " + tab.getPosition());

                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 1) {
                    requireActivity().setTitle(R.string.nameCardWords);
                } else {
                    requireActivity().setTitle(R.string.nameTests);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Log.i(TAG, "onPageSelected - " + position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }

    @Override
    public void onRefresh() {
        viewAdapter.refreshFragment(viewPager.getCurrentItem());
    }
}