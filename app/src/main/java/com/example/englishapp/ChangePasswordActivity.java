package com.example.englishapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BlendMode;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText currentPassword, newPassword, newConfirmedPassword;
    private TextView tvAuthentication;
    private Button btnChange, btnAuthenticate;
    private ProgressBar pbAuthenticate, pbChange;
    private String userPassword;

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPassword = currentPassword.getText().toString();

                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    currentPassword.setError("Password is required");
                    currentPassword.requestFocus();
                } else {
                    pbAuthenticate.setVisibility(View.VISIBLE);

                    //ReAuthentication
                    AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPassword);
                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pbAuthenticate.setVisibility(View.GONE);

                                //Disable
                                currentPassword.setEnabled(false);
                                btnAuthenticate.setEnabled(false);
                                newPassword.setEnabled(true);
                                newConfirmedPassword.setEnabled(true);
                                btnChange.setEnabled(true);

                                tvAuthentication.setText("You were successfully authenticated");
                                Toast.makeText(ChangePasswordActivity.this, "Password has been verified", Toast.LENGTH_SHORT).show();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    btnChange.setBackgroundTintBlendMode(BlendMode.COLOR_BURN);
                                }

                                btnChange.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changeUserPassword(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                pbAuthenticate.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void changeUserPassword(FirebaseUser firebaseUser) {
        String userNewPassword = newPassword.getText().toString();
        String userNewConfirmedPassword = newConfirmedPassword.getText().toString();

        if (TextUtils.isEmpty(userNewPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            newPassword.setError("Password is required");
            newPassword.requestFocus();
        } else if (userNewPassword.length() < 6) {
            Toast.makeText(ChangePasswordActivity.this, "Password too weak", Toast.LENGTH_SHORT).show();
            newPassword.setError("Password must be at least 6 digits");
            newPassword.requestFocus();
        } else if (!userNewPassword.equals(userNewConfirmedPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
            newConfirmedPassword.setError("Enter same password");
            newConfirmedPassword.requestFocus();
        } else if (userPassword.equals(userNewPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "New password cannot be same as old", Toast.LENGTH_SHORT).show();
            newConfirmedPassword.setError("Please enter new password");
            newConfirmedPassword.requestFocus();
        } else {
            pbChange.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        pbChange.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id.userPassword);
        newPassword = findViewById(R.id.userNewPassword);
        newConfirmedPassword = findViewById(R.id.userNewConfirmedPassword);
        tvAuthentication = findViewById(R.id.updatePasswordVerification);
        pbAuthenticate = findViewById(R.id.progressBarAuthenticate);
        pbChange = findViewById(R.id.progressBarUpdate);
        btnAuthenticate = findViewById(R.id.btnAuthenticate);
        btnChange = findViewById(R.id.btnUpdatePassword);

        //Disabled
        newPassword.setEnabled(false);
        newConfirmedPassword.setEnabled(false);
        btnChange.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
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
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menuUpdateEmail) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
//        } else if (id == R.id.menuSettings) {
//            Intent intent = new Intent(ChangePasswordActivity.this, ActivitySettings.class);
//            startActivity(intent);
//            finish();
        } else if (id == R.id.menuChangePassword) {
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menuDeleteAccount) {
            Intent intent = new Intent(ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menuLogOut) {
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, GreetingActivity.class);

            //Clear stack
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}