package com.example.englishapp.Authentication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

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

            if (getFragmentManager().getBackStackEntryCount() > 1){
                Toast.makeText(this, "CLICKED-1", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStackImmediate();
            } else {
                Toast.makeText(this, "CLICKED-2", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
