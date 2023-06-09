package com.example.englishapp.activities;

import static com.example.englishapp.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.database.Constants.KEY_IS_WORDS;
import static com.example.englishapp.database.Constants.KEY_TEST_TIME;
import static com.example.englishapp.database.Constants.KEY_USER_UID;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.database.Constants;
import com.example.englishapp.database.DataBaseUsers;
import com.example.englishapp.fragments.CategoryFragment;
import com.example.englishapp.fragments.ChatFragment;
import com.example.englishapp.fragments.DiscussFragment;
import com.example.englishapp.fragments.LeaderBordFragment;
import com.example.englishapp.fragments.MapUsersFragment;
import com.example.englishapp.fragments.ProfileFragment;
import com.example.englishapp.fragments.ProfileInfoDialogFragment;
import com.example.englishapp.fragments.ScoreFragment;
import com.example.englishapp.interfaces.TasksChecking;
import com.example.englishapp.repositories.LocationManager;
import com.example.englishapp.models.UserModel;
import com.example.englishapp.repositories.TasksRepository;
import com.example.englishapp.services.ForegroundLocationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends BaseActivity {
    private static final String TAG = "ActivityMain";
    private FrameLayout mainFrame;

    private Toolbar toolbar;
    private TextView textClose;
    private Button btnOpenSettings;
    private Dialog progressLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setListeners();

        // start CategoryFragment at first
        setFragment(new CategoryFragment());

        receiveData();

        startLocationService();

    }

    private void startLocationService() {
        if(!LocationManager.getInstance(this).isLocationEnabled()) {
            progressLocation.show();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(MainActivity.this, ForegroundLocationService.class);

            startForegroundService(intent);
        }

    }

    private void setListeners() {
        textClose.setOnClickListener(v -> progressLocation.dismiss());

        btnOpenSettings.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));

        btnOpenSettings.setOnClickListener(v -> {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            progressLocation.dismiss();
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    private void receiveData() {
        Intent intent = getIntent();

        new TasksRepository().checkTasks(intent, new TasksChecking() {
            @Override
            public void showDialog() {
                new ProfileInfoDialogFragment().show(getSupportFragmentManager(), SHOW_FRAGMENT_DIALOG);
            }

            @Override
            public void showMap() {
                MapUsersFragment fragment = new MapUsersFragment();
                setFragment(fragment);
            }

            @Override
            public void showDiscussion() {
                UserModel receivedUser = new DataBaseUsers().findUserById(intent.getStringExtra(KEY_USER_UID));

                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_CHOSEN_USER_DATA, receivedUser);
                DiscussFragment fragment = new DiscussFragment();
                fragment.setArguments(bundle);

                setFragment(fragment);
            }

            @Override
            public void showProfile() {
                ProfileFragment fragment = new ProfileFragment();
                setFragment(fragment);
            }

            @Override
            public void checkingExam() {
                ScoreFragment fragment = new ScoreFragment();

                Bundle bundle = new Bundle();

                bundle.putLong(Constants.KEY_TEST_TIME, intent.getLongExtra(KEY_TEST_TIME, -1));
                bundle.putBoolean(KEY_IS_WORDS, intent.getBooleanExtra(KEY_IS_WORDS, false));

                fragment.setArguments(bundle);

                setFragment(fragment);
            }
        });

    }

    private void init() {

        progressLocation = new Dialog(this);
        progressLocation.setContentView(R.layout.dialog_check_location);
        progressLocation.setCancelable(false);
        progressLocation.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressLocation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textClose = progressLocation.findViewById(R.id.textCancel);
        btnOpenSettings = progressLocation.findViewById(R.id.btnOpenSettings);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);
        getSupportActionBar().setHomeButtonEnabled(true);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavBar);
        mainFrame = findViewById(R.id.nav_host_fragment_content_feed);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home_menu) {
                setFragment(new CategoryFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_chat_menu) {
                setFragment(new ChatFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_leader_menu) {
                setFragment(new LeaderBordFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_account_menu) {
                setFragment(new ProfileFragment());
                return true;
            }

            return false;
        });

    }

    public void setFragment(Fragment fragment) {

        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).show();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(mainFrame.getId(), fragment)
                .addToBackStack(String.valueOf(fragment.getId()))
                .commit();
    }

    public void setTitle(int strId) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(strId));
    }

    @Override
    public void setTitle(CharSequence title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Log.i(TAG, "Stack of Fragments - " + getSupportFragmentManager().getBackStackEntryCount());

            if (getSupportFragmentManager().getBackStackEntryCount() > 1){

                getSupportFragmentManager().popBackStackImmediate();

            } else {

                MainActivity.this.finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}