package com.example.englishapp.presentation.activities;

import static com.example.englishapp.data.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.data.database.Constants.KEY_IS_WORDS;
import static com.example.englishapp.data.database.Constants.KEY_TEST_TIME;
import static com.example.englishapp.data.database.Constants.KEY_USER_UID;
import static com.example.englishapp.data.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.englishapp.R;
import com.example.englishapp.data.database.Constants;
import com.example.englishapp.data.database.DataBase;
import com.example.englishapp.data.database.DataBaseUsers;
import com.example.englishapp.data.models.UserModel;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.interfaces.RefreshListener;
import com.example.englishapp.domain.interfaces.TasksChecking;
import com.example.englishapp.domain.repositories.LocationManager;
import com.example.englishapp.domain.repositories.TasksRepository;
import com.example.englishapp.domain.services.ForegroundLocationService;
import com.example.englishapp.presentation.fragments.CategoryFragment;
import com.example.englishapp.presentation.fragments.ChatFragment;
import com.example.englishapp.presentation.fragments.DiscussFragment;
import com.example.englishapp.presentation.fragments.LeaderBordFragment;
import com.example.englishapp.presentation.fragments.MapUsersFragment;
import com.example.englishapp.presentation.fragments.ProfileFragment;
import com.example.englishapp.presentation.fragments.ProfileInfoDialogFragment;
import com.example.englishapp.presentation.fragments.ScoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends BaseActivity {
    private static final String TAG = "ActivityMain";
    private FrameLayout mainFrame;
    private Toolbar toolbar;
    private TextView textClose;
    private Button btnOpenSettings;
    private Dialog progressLocation;
    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final Set<RefreshListener> refreshListeners = new HashSet<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setListeners();

        // start CategoryFragment at first
        setFragment(new CategoryFragment(), true);

        receiveData();

        startLocationService();

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

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        mainFrame = findViewById(R.id.nav_host_fragment_content_feed);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home_menu) {
                setFragment(new CategoryFragment(), true);
                return true;
            } else if (item.getItemId() == R.id.nav_chat_menu) {
                setFragment(new ChatFragment(), true);
                return true;
            } else if (item.getItemId() == R.id.nav_leader_menu) {
                setFragment(new LeaderBordFragment(), true);
                return true;
            } else if (item.getItemId() == R.id.nav_account_menu) {
                setFragment(new ProfileFragment(), true);
                return true;
            }

            return false;
        });

    }

    private void setListeners() {
        textClose.setOnClickListener(v -> progressLocation.dismiss());

        btnOpenSettings.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));

        btnOpenSettings.setOnClickListener(v -> {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            progressLocation.dismiss();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> new DataBase().loadData(new CompleteListener() {
            @Override
            public void OnSuccess() {

                Log.i(TAG, "amount of listeners - " + refreshListeners.size());

                for (RefreshListener listener: refreshListeners) {
                    listener.onRefresh();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void OnFailure() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }));

        int color1 = getResources().getColor(com.google.android.material.R.color.design_default_color_primary);
        int color2 = getResources().getColor(R.color.secondary_color);
        int color3 = getResources().getColor(R.color.red);

        swipeRefreshLayout.setColorSchemeColors(color1, color2, color3);
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
                setFragment(fragment, false);
            }

            @Override
            public void showDiscussion() {
                UserModel receivedUser = new DataBaseUsers().findUserById(intent.getStringExtra(KEY_USER_UID));

                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_CHOSEN_USER_DATA, receivedUser);
                DiscussFragment fragment = new DiscussFragment();
                fragment.setArguments(bundle);

                setFragment(fragment, false);
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

                setFragment(fragment, false);
            }
        });

    }

    public void setCheckedNavigationIcon(int number) {
        bottomNavigationView.getMenu().getItem(number).setChecked(true);
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

    public void setFragment(Fragment fragment, boolean isRefreshing) {

        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).show();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(mainFrame.getId(), fragment)
                .addToBackStack(String.valueOf(fragment.getId()))
                .commit();

        swipeRefreshLayout.setEnabled(isRefreshing);
    }

    public void setTitle(int strId) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(strId));
    }

    public void addListener(RefreshListener listener) {
        refreshListeners.add(listener);
    }

    public void removeListener(RefreshListener listener) {
        refreshListeners.remove(listener);
    }

    @Override
    public void setTitle(CharSequence title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Stack of Fragments - " + getSupportFragmentManager().getBackStackEntryCount());

        if (getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            MainActivity.this.finish();
        }
    }
}