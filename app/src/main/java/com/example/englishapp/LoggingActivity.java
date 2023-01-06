package com.example.englishapp;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

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
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    // check if email is verified before user can access their profile
                    if (firebaseUser.isEmailVerified()) {
                        Toast.makeText(LoggingActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();

                        //Open Profile
                        Intent intent = new Intent(LoggingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }
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

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoggingActivity.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

        //Open Email app
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                // to open in new window and not in app
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

        //Show or Hide using Eye
        ImageView imageEye = findViewById(R.id.imageEye);
        userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        imageEye.setImageResource(R.drawable.hidden);

        imageEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //if visible, hide it
                    userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageEye.setImageResource(R.drawable.hidden);
                } else {
                    userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageEye.setImageResource(R.drawable.view);
                }
            }
        });

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
    protected void onStart() {
        //If user is already logged in
        super.onStart();

        if(authProfile.getCurrentUser() != null) {
            Toast.makeText(LoggingActivity.this, "You are already logged in", Toast.LENGTH_SHORT).show();

            // go to profile
            Intent intent = new Intent(LoggingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoggingActivity.this, "You are not already logged in", Toast.LENGTH_SHORT).show();
        }
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
