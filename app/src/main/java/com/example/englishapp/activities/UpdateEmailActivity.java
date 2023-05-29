package com.example.englishapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BlendMode;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBarAuthenticated, progressBarUpdate;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPwd;
    private Button btnUpdateEmail, btnAuthenticated;
    private EditText userEmail, userPassword;


    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Verify email
                    firebaseUser.sendEmailVerification();
                    //Toast.makeText(UpdateEmailActivity.this, String.valueOf(firebaseUser.isEmailVerified()), Toast.LENGTH_SHORT).show();

                    Toast.makeText(UpdateEmailActivity.this, "Email has been updated. Please verify your new email", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UpdateEmailActivity.this, ProfileActivity.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBarUpdate.setVisibility(View.GONE);
            }
        });
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        btnAuthenticated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwd = userPassword.getText().toString();

                if(TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(UpdateEmailActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                    userPassword.setError("Enter the password for authentication");
                    userPassword.requestFocus();
                } else {
                    progressBarAuthenticated.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBarAuthenticated.setVisibility(View.GONE);

                                Toast.makeText(UpdateEmailActivity.this, "Password was verified", Toast.LENGTH_SHORT).show();

                                //Set enable
                                textViewAuthenticated.setText("You are authenticated. You can update your email");
                                userEmail.setEnabled(true);
                                btnUpdateEmail.setEnabled(true);
                                userPassword.setEnabled(false);
                                btnAuthenticated.setEnabled(false);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    btnUpdateEmail.setBackgroundTintBlendMode(BlendMode.COLOR_BURN);
                                }

                                btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = userEmail.getText().toString();
                                        if(TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                                            userEmail.setError("Email is required");
                                            userEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                                            userEmail.setError("Valid email is required");
                                            userEmail.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New email cannot be same as old", Toast.LENGTH_SHORT).show();
                                            userEmail.setError("Enter new email");
                                            userEmail.requestFocus();
                                        } else {
                                            progressBarUpdate.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        progressBarAuthenticated = findViewById(R.id.progressBarAuthenticate);
        progressBarUpdate = findViewById(R.id.progressBarUpdate);
        textViewAuthenticated = findViewById(R.id.updateEmailVerification);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        btnAuthenticated = findViewById(R.id.btnAuthenticate);
        btnUpdateEmail = findViewById(R.id.btnUpdateEmail);

        // Set enabled
        btnUpdateEmail.setEnabled(false);
        userEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textViewUserEmail);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser == null) {
            Toast.makeText(UpdateEmailActivity.this, "User details does not available", Toast.LENGTH_SHORT).show();
        } else {
            reAuthenticate(firebaseUser);
        }
    }


    //ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate Menu
        getMenuInflater().inflate(R.menu.common_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Selected item
        int id = item.getItemId();

        if (id == R.id.menuRefresh) {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.menuUpdateProfile) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menuUpdateEmail) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
//        } else if (id == R.id.menuSettings) {
//            Intent intent = new Intent(ProfileActivity.this, ActivitySettings.class);
//            startActivity(intent);
//            finish();
        } else if (id == R.id.menuChangePassword) {
            Intent intent = new Intent(UpdateEmailActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
//        } else if (id == R.id.menuDeleteAccount) {
//            Intent intent = new Intent(ProfileActivity.this, DeleteProfileActivity.class);
//            startActivity(intent);
//            finish();
        } else if (id == R.id.menuLogOut) {
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateEmailActivity.this, GreetingActivity.class);

            //Clear stack
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}