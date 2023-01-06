package com.example.englishapp;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoggingActivity extends AppCompatActivity {
    private EditText userEmail, userPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoggingActivity";

    private void loginUser(String textEmail, String textPassword){
        authProfile.signInWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(LoggingActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoggingActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        userPassword.setError("User does not exist or is no longer valid");
                        userPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        userPassword.setError("Invalid credentials. Kindly, check and re-enter");
                        userPassword.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoggingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(LoggingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);

        Window window = getWindow();
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        //Login user
        Button btnLogging = findViewById(R.id.btnLogging);
        btnLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = userEmail.getText().toString();
                String textPassword = userPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoggingActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    userEmail.setError("Email is required");
                    userEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoggingActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    userEmail.setError("Email is not valid");
                    userEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)){
                    Toast.makeText(LoggingActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    userEmail.setError("Password is required");
                    userEmail.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPassword);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(LoggingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {

        }
    }
}
