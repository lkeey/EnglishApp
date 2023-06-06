package com.example.englishapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.englishapp.R;
import com.example.englishapp.adapters.LearningPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class SplashLearningFragment extends Fragment {

    private static final String TAG = "SplashLearningFragment";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

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

        LearningPagerAdapter viewAdapter = new LearningPagerAdapter(requireActivity());
        viewPager.setAdapter(viewAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabSelected - " + tab.getPosition());

                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 1:
                        requireActivity().setTitle(R.string.nameCardWords);
                        break;

                    default:
                        requireActivity().setTitle(R.string.nameTests);
                        break;
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
}