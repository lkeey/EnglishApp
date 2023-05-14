package com.example.englishapp.MVP;

import static com.example.englishapp.MVP.DataBase.LIST_OF_USERS;
import static com.example.englishapp.MVP.DataBase.findUserById;
import static com.example.englishapp.messaging.Constants.KEY_CHECK_LOCATION;
import static com.example.englishapp.messaging.Constants.KEY_CHOSEN_USER_DATA;
import static com.example.englishapp.messaging.Constants.KEY_USER_UID;
import static com.example.englishapp.messaging.Constants.SHOW_FRAGMENT_DIALOG;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.example.englishapp.Authentication.CategoryFragment;
import com.example.englishapp.Authentication.ProfileInfoDialogFragment;
import com.example.englishapp.Authentication.ProfileInfoFragment;
import com.example.englishapp.R;
import com.example.englishapp.alarm.AlarmReceiver;
import com.example.englishapp.chat.BaseActivity;
import com.example.englishapp.chat.ChatFragment;
import com.example.englishapp.chat.DiscussFragment;
import com.example.englishapp.chat.MainChatFragment;
import com.example.englishapp.chat.MapUsersFragment;
import com.example.englishapp.location.LocationManager;
import com.example.englishapp.location.PermissionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FeedActivity extends BaseActivity {
    private static final String TAG = "ActivityFeed";
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
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        init();

        setListeners();

        receiveData();

//        showDialogLocation();

        // start CategoryFragment at first
        setFragment(new CategoryFragment());

    }

    private void setListeners() {
        textClose.setOnClickListener(v -> progressLocation.dismiss());

        btnOpenSettings.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));

        btnOpenSettings.setOnClickListener(v -> {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            progressLocation.dismiss();
        });
    }

    private void showDialogLocation() {

        if(!LocationManager.getInstance(this).isLocationEnabled()) {
            progressLocation.show();
        }

        try {
            PermissionManager permissionManager = PermissionManager.getInstance(this);

            permissionManager.askPermissions(FeedActivity.this, foreground_location_permissions, 1);

            if (!permissionManager.checkPermissions(background_location_permission)) {
                Log.i(TAG, String.valueOf(permissionManager.checkPermissions(background_location_permission)));
                permissionManager.askPermissions(FeedActivity.this, background_location_permission, 2);
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intent = new Intent(FeedActivity.this, AlarmReceiver.class);
            intent.putExtra(KEY_CHECK_LOCATION, true);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(FeedActivity.this, 1, intent, PendingIntent.FLAG_MUTABLE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 15 * 100, pendingIntent);

            Log.i(TAG, "Successfully set");

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

//    private void showDialogLocationOld() {
//        Log.i(TAG, "Enable - " + LocationManager.getInstance(this).isLocationEnabled());
//
//        JobScheduler jobScheduler = getSystemService(JobScheduler.class);
//        ComponentName componentName = new ComponentName(this, LocationService.class);
//        JobInfo.Builder info = new JobInfo.Builder(1111, componentName);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            info.setRequiresBatteryNotLow(true);
//        }
//
//        info.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
//
//        info.setPeriodic(1*60*100); // 1 minute
//        info.setMinimumLatency(100);
//
//        if (jobScheduler != null) {
//            int result = jobScheduler.schedule(info.build());
//
//            if (result == JobScheduler.RESULT_SUCCESS) {
//                Log.i(TAG, "Job started");
//            } else {
//                Log.i(TAG, "Can not start job");
//            }
//
//        }
//    }
//
//    private void startCheckingPosition() {
//        PermissionManager permissionManager = PermissionManager.getInstance(this);
//        LocationManager locationManager = LocationManager.getInstance(this);
//
//        permissionManager.askPermissions(FeedActivity.this, foreground_location_permissions, 1);
//
//        if (!permissionManager.checkPermissions(background_location_permission)) {
//            Log.i(TAG, String.valueOf(permissionManager.checkPermissions(background_location_permission)));
//            permissionManager.askPermissions(FeedActivity.this, background_location_permission, 2);
//
//        } else {
//
//            if (locationManager.isLocationEnabled()) {
//                Log.i(TAG, "location enable");
//
//                locationManager.createLocationRequest();
//
//                startLocationWork();
//
//            } else {
//                Log.i(TAG, "location does not enabled");
//
//                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//                locationManager.createLocationRequest();
//
//                startLocationWork();
//
//                Toast.makeText(this, "Please turn on your location", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void startLocationWork() {
//        Log.i(TAG, "startLocationWork");
//
//        OneTimeWorkRequest foregroundWorkRequest = new OneTimeWorkRequest.Builder(LocationWork.class)
//                .addTag("LocationWork")
//                .setBackoffCriteria(
//                        BackoffPolicy.LINEAR,
//                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
//                        TimeUnit.SECONDS
//                ).build();
//
//        WorkManager.getInstance(FeedActivity.this).enqueue(foregroundWorkRequest);
//    }

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

        progressLocation = new Dialog(this);
        progressLocation.setContentView(R.layout.dialog_check_location);
        progressLocation.setCancelable(false);
        progressLocation.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressLocation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textClose = progressLocation.findViewById(R.id.textCancel);
        btnOpenSettings = progressLocation.findViewById(R.id.btnOpenSettings);

        toolbar = findViewById(R.id.toolbar);

        Log.i(TAG, "Toolbar found");

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
                    setFragment(new ProfileInfoFragment());
                    return true;

                case R.id.nav_chat_menu:
                    setFragment(new MainChatFragment());
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