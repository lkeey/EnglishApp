package com.example.englishapp.activities;

import static com.example.englishapp.database.DataBase.LIST_OF_USERS;
import static com.example.englishapp.database.DataBase.findUserById;
import static com.example.englishapp.database.Constants.KEY_CHECK_LOCATION;
import static com.example.englishapp.database.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.database.Constants.KEY_TEST_TIME;
import static com.example.englishapp.database.Constants.KEY_USER_UID;
import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
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
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.englishapp.models.UserModel;
import com.example.englishapp.fragments.CategoryFragment;
import com.example.englishapp.fragments.ProfileFragment;
import com.example.englishapp.fragments.ProfileInfoDialogFragment;
import com.example.englishapp.R;
import com.example.englishapp.alarm.AlarmReceiver;
import com.example.englishapp.fragments.ChatFragment;
import com.example.englishapp.fragments.DiscussFragment;
import com.example.englishapp.services.LocationService;
import com.example.englishapp.managers.LocationManager;
import com.example.englishapp.location.LocationWork;
import com.example.englishapp.managers.PermissionManager;
import com.example.englishapp.database.Constants;
import com.example.englishapp.fragments.LeaderBordFragment;
import com.example.englishapp.fragments.ScoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {
    private static final String TAG = "ActivityMain";
    private BottomNavigationView bottomNavigationView;
    private FrameLayout mainFrame;
    private final String[] foreground_location_permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private final String[] background_location_permission = {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    private Toolbar toolbar;
    private TextView textClose;
    private Button btnOpenSettings;
    private Dialog progressLocation;
    private NavigationBarView.OnItemSelectedListener onNavigationItemSelectedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setListeners();

        // start CategoryFragment at first
        setFragment(new CategoryFragment());

        receiveData();

        startCheckingPosition();

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

    private void showDialogLocation() {

        if(!LocationManager.getInstance(this).isLocationEnabled()) {
            progressLocation.show();
        }

        try {
            PermissionManager permissionManager = PermissionManager.getInstance(this);

            permissionManager.askPermissions(MainActivity.this, foreground_location_permissions, 1);

            if (!permissionManager.checkPermissions(background_location_permission)) {
                Log.i(TAG, String.valueOf(permissionManager.checkPermissions(background_location_permission)));
                permissionManager.askPermissions(MainActivity.this, background_location_permission, 2);
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            intent.putExtra(KEY_CHECK_LOCATION, true);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_MUTABLE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2 * 60 * 100, pendingIntent);

            Log.i(TAG, "Successfully set");

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void showDialogLocationOld() {
        Log.i(TAG, "Enable - " + LocationManager.getInstance(this).isLocationEnabled());

        JobScheduler jobScheduler = getSystemService(JobScheduler.class);
        ComponentName componentName = new ComponentName(this, LocationService.class);
        JobInfo.Builder info = new JobInfo.Builder(1111, componentName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info.setRequiresBatteryNotLow(true);
        }

        info.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);

        info.setPeriodic(1*60*100);
        info.setMinimumLatency(100);

        if (jobScheduler != null) {
            int result = jobScheduler.schedule(info.build());

            if (result == JobScheduler.RESULT_SUCCESS) {
                Log.i(TAG, "Job started");
            } else {
                Log.i(TAG, "Can not start job");
            }

        }
    }

    private void startCheckingPosition() {

        if(!LocationManager.getInstance(this).isLocationEnabled()) {
            progressLocation.show();
        }

        PermissionManager permissionManager = PermissionManager.getInstance(this);
        LocationManager locationManager = LocationManager.getInstance(this);

        permissionManager.askPermissions(MainActivity.this, foreground_location_permissions, 1);

        locationManager.createLocationRequest();

        startLocationWork();
    }

    private void startLocationWork() {
        Log.i(TAG, "startLocationWork");

        OneTimeWorkRequest foregroundWorkRequest = new OneTimeWorkRequest.Builder(LocationWork.class)
                .addTag("LocationWork")
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.SECONDS
                ).build();

        WorkManager.getInstance(MainActivity.this).enqueue(foregroundWorkRequest);
    }

    private void receiveData() {
        try {
            Intent intent = getIntent();

            boolean status = intent.getBooleanExtra(SHOW_FRAGMENT_DIALOG, false);
            String userUID = intent.getStringExtra(KEY_USER_UID);
            long totalTime = intent.getLongExtra(KEY_TEST_TIME, -1);

            Log.i(TAG, "STATUS " + status);
            Log.i(TAG, "UserUID - " + userUID + " - " + LIST_OF_USERS.size());
            Log.i(TAG, "totalTime - " + totalTime);

            if (status) {
                new ProfileInfoDialogFragment().show(getSupportFragmentManager(), SHOW_FRAGMENT_DIALOG);

            } else if (userUID != null) {

                UserModel receivedUser = findUserById(userUID);

                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_CHOSEN_USER_DATA, receivedUser);
                DiscussFragment fragment = new DiscussFragment();
                fragment.setArguments(bundle);

                setFragment(fragment);

            } else if (totalTime != -1L) {

                Log.i(TAG, "show ScoreFragment");

                ScoreFragment fragment = new ScoreFragment();

                Bundle bundle = new Bundle();

                bundle.putLong(Constants.KEY_TEST_TIME, totalTime);

                fragment.setArguments(bundle);

                setFragment(fragment);

            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
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
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);
        getSupportActionBar().setHomeButtonEnabled(true);

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        mainFrame = findViewById(R.id.nav_host_fragment_content_feed);

        onNavigationItemSelectedListener = item -> {

            switch (item.getItemId()) {
                case R.id.nav_home_menu:
//                    setFragment(new ProfileInfoFragment());

                    setFragment(new CategoryFragment());

                    return true;

                case R.id.nav_chat_menu:

                    setFragment(new ChatFragment());

                    return true;

                case R.id.nav_leader_menu:

                    setFragment(new LeaderBordFragment());

                    return true;

                case R.id.nav_account_menu:

                    setFragment(new ProfileFragment());

                    return true;

            }
            return false;
        };

        bottomNavigationView.setOnItemSelectedListener(onNavigationItemSelectedListener);

    }

    public void setFragment(Fragment fragment) {

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getSupportActionBar().show();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(mainFrame.getId(), fragment)
                .addToBackStack(String.valueOf(fragment.getId()))
                .commit();
    }

    public void setTitle(int strId) {
        getSupportActionBar().setTitle(getString(strId));
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {

        setSupportActionBar(toolbar);

        getSupportActionBar().show();

//        Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "clicked");

        super.onBackPressed();
    }
}