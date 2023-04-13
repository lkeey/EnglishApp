package com.example.englishapp.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;

public class MainAuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "Activity Authentication";
    private Toolbar toolbar;
    private static FrameLayout mainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_authentication);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainFrame = findViewById(R.id.mainFrame);

        Intent intent = new Intent(MainAuthenticationActivity.this, OTPVerification.class);
        startActivity(intent);

        setTitle(R.string.nameLogin);
        setFragment(new LoginFragment());
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(mainFrame.getId(), fragment)
                .addToBackStack(String.valueOf(fragment.getId()))
                .commit();
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
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
