package com.example.englishapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.fragments.LoginFragment;

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Log.i(TAG, "Stack of Fragments - " + getSupportFragmentManager().getBackStackEntryCount());

            if (getSupportFragmentManager().getBackStackEntryCount() > 1){

                getSupportFragmentManager().popBackStackImmediate();

            } else {

                MainAuthenticationActivity.this.finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
