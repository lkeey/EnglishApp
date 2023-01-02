package com.example.englishapp;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        // go to account
        Button btnStart = findViewById(R.id.btnLogging);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, LoggingActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {

                }
            }
        });

        //begin Learning
        Button btnLearning = findViewById(R.id.btnStart);
        btnLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, LoggingActivity.class);
                    startActivity(intent);
                    finish();

                    ArrayActivity ac = new ArrayActivity(
                            new String[]{"awd"}, new String[]{"AWd"}
                    );

//                    Toast.makeText(MainActivity.this, "Good3", Toast.LENGTH_SHORT).show();
//
//                    ac.createWallPaper();
//

                    int reqCode = 1;
                    Intent intentNew = new Intent(getApplicationContext(), MainActivity.class);
                    ac.showNotification(getApplicationContext(), "Title", "Message", intentNew, reqCode);

                    Toast.makeText(MainActivity.this, "Good4", Toast.LENGTH_SHORT).show();

                } catch ( Exception e) {

                }
            }
        });


        //Pass the Exam
        Button btnCheck = findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, CheckingActivity.class);
                    startActivity(intent);
                    finish();
                } catch ( Exception e) {

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();

    }
}