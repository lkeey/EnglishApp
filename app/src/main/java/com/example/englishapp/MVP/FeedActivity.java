package com.example.englishapp.MVP;

import static com.example.englishapp.MVP.DataBase.LIST_OF_USERS;
import static com.example.englishapp.MVP.DataBase.findUserById;
import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.messaging.Constants.KEY_USER_UID;
import static com.example.englishapp.messaging.Constants.SHOW_FRAGMENT_DIALOG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.englishapp.Authentication.ProfileInfoDialogFragment;
import com.example.englishapp.Authentication.ProfileInfoFragment;
import com.example.englishapp.R;
import com.example.englishapp.chat.BaseActivity;
import com.example.englishapp.chat.ChatFragment;
import com.example.englishapp.chat.DiscussFragment;
import com.example.englishapp.chat.MapUsersFragment;
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

        receiveData();

    }

    private void receiveData() {
        try {
            Intent intent = getIntent();

            boolean status = intent.getBooleanExtra(SHOW_FRAGMENT_DIALOG, false);
            String userUID = intent.getStringExtra(KEY_USER_UID);

            Log.i(TAG, "STATUS " + status);
            Log.i(TAG, "UserUID - " + userUID + LIST_OF_USERS.size());

            if (status) {
                new ProfileInfoDialogFragment().show(getSupportFragmentManager(), SHOW_FRAGMENT_DIALOG);

            } else if (userUID != null) {

                UserModel receivedUser = findUserById(userUID);

                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_CHOSEN_USER_DATA, receivedUser);
                DiscussFragment fragment = new DiscussFragment();
                fragment.setArguments(bundle);

                setFragment(fragment);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void init() {
        try {
            toolbar = findViewById(R.id.toolbar);

            Log.i(TAG, "Toolbar found");

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Home");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);
            getSupportActionBar().setHomeButtonEnabled(true);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


        bottomNavigationView = findViewById(R.id.bottomNavBar);
        mainFrame = findViewById(R.id.nav_host_fragment_content_feed);

        onNavigationItemSelectedListener = item -> {

            switch (item.getItemId()) {
                case R.id.nav_home_menu:
                    setFragment(new ProfileInfoFragment());
                    return true;

                case R.id.nav_leader_menu:
                    setFragment(new ChatFragment());
                    return true;

                case R.id.nav_account_menu:
                        setFragment(new MapUsersFragment());
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

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


}