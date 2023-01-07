package com.example.englishapp;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView userGreeting, userName, userEmail, userDOB, userGender, userMobile;
    private ProgressBar progressBar;
    private String textName, textEmail, textDOB, textGender, textMobile;
    private ImageView userImage;
    private FirebaseAuth authProfile;

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting user's reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readWriteUserDetails != null) {
                    textName = firebaseUser.getDisplayName();
                    textEmail = firebaseUser.getEmail();
                    textDOB = readWriteUserDetails.userDOB;
                    textGender = readWriteUserDetails.userGender;
                    textMobile = readWriteUserDetails.userMobile;

                    userGreeting.setText("Welcome, " + textName);
                    userName.setText(textName);
                    userEmail.setText(textEmail);
                    userDOB.setText(textDOB);
                    userGender.setText(textGender);
                    userMobile.setText(textMobile);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void checkEmailVerification(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time");

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
        setContentView(R.layout.activity_profile);

        Window window = getWindow();
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        userGreeting = findViewById(R.id.textGreeting);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userDOB = findViewById(R.id.userDOB);
        userGender = findViewById(R.id.userGender);
        userMobile = findViewById(R.id.userMobile);

        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null) {
            Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        } else {
            checkEmailVerification(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
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
//        } else if (id == R.id.menuUpdateProfile) {
//            Intent intent = new Intent(ProfileActivity.this, UpdateProfile.class);
//            startActivity(intent);
//            finish();
//        } else if (id == R.id.menuUpdateEmail) {
//            Intent intent = new Intent(ProfileActivity.this, UpdateEmail.class);
//            startActivity(intent);
//            finish();
//        } else if (id == R.id.menuSettings) {
//            Intent intent = new Intent(ProfileActivity.this, ActivitySettings.class);
//            startActivity(intent);
//            finish();
//        } else if (id == R.id.menuChangePassword) {
//            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
//            startActivity(intent);
//            finish();
//        } else if (id == R.id.menuDeleteAccount) {
//            Intent intent = new Intent(ProfileActivity.this, DeleteProfileActivity.class);
//            startActivity(intent);
//            finish();
        } else if (id == R.id.menuLogOut) {
            authProfile.signOut();
            Toast.makeText(ProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, GreetingActivity.class);

            //Clear stack
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
