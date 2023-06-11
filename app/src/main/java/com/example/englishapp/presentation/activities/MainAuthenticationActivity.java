package com.example.englishapp.presentation.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.presentation.fragments.LoginFragment;

import java.util.Objects;

public class MainAuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "ActivityAuthentication";
    private FrameLayout mainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_authentication);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);

        mainFrame = findViewById(R.id.mainFrame);

        setTitle(R.string.nameLogin);
        setFragment(new LoginFragment());
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(mainFrame.getId(), fragment)
            .addToBackStack(String.valueOf(fragment.getId()))
            .commit();
    }

    public void setTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Stack of Fragments - " + getSupportFragmentManager().getBackStackEntryCount());

        if (getSupportFragmentManager().getBackStackEntryCount() > 1){

            getSupportFragmentManager().popBackStackImmediate();

        } else {
            MainAuthenticationActivity.this.finish();
        }
    }
}
