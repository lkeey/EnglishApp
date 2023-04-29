package com.example.englishapp.MVP;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.chat.BaseActivity;
import com.example.englishapp.chat.ChatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FeedActivity extends BaseActivity {
    private static final String TAG = "ActivityFeed";
    private BottomNavigationView bottomNavigationView;
    private FrameLayout mainFrame;
    private Toolbar toolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        mainFrame = findViewById(R.id.nav_host_fragment_content_feed);

        onNavigationItemSelectedListener = item -> {

            switch (item.getItemId()) {
                case R.id.nav_home_menu:
//                        setFragment(new CategoryFragment());
                    return true;

                case R.id.nav_leader_menu:
                    setFragment(new ChatFragment());
                    return true;

                case R.id.nav_account_menu:
//                        setFragment(new AccountFragment());
                    return true;
            }
            return false;
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(mainFrame.getId(), fragment)
                .addToBackStack(String.valueOf(fragment.getId()))
                .commit();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Log.i(TAG, "Stack of Fragments - " + getFragmentManager().getBackStackEntryCount());

            if (getFragmentManager().getBackStackEntryCount() > 2){
                getFragmentManager().popBackStackImmediate();

            } else {
                super.onBackPressed();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}