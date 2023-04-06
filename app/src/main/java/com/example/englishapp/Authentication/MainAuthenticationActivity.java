package com.example.englishapp.Authentication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.englishapp.R;

public class MainAuthenticationActivity extends AppCompatActivity {

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainFrame.getId(), fragment);
        transaction.commit();
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
        }

        return super.onOptionsItemSelected(item);
    }

}
