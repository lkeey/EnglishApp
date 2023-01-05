package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName, userEmail, userDOB,
            userMobile, userPassword,
            userConfirmedPassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupGender;
    private RadioButton radioBtnGender;

    private void registerUser(String textName, String textEmail, String textDOB, String textGender, String textMobile, String textPassword){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, task -> {
            if(task.isSuccessful()){
                Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                FirebaseUser user = auth.getCurrentUser();

                //Send verification email
                user.sendEmailVerification();

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userDOB = findViewById(R.id.userDOB);
        userMobile = findViewById(R.id.userMobile);
        userPassword = findViewById(R.id.userPassword);
        userConfirmedPassword = findViewById(R.id.userConfirmedPassword);

        radioGroupGender = findViewById(R.id.groupGender);
        radioGroupGender.clearCheck();

        progressBar = findViewById(R.id.progressBar);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> {
            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            radioBtnGender = findViewById(selectedGenderId);

            String textName = userName.getText().toString();
            String textEmail = userEmail.getText().toString();
            String textDOB = userDOB.getText().toString();
            String textMobile = userMobile.getText().toString();
            String textPassword = userPassword.getText().toString();
            String textConfirmedPassword = userConfirmedPassword.getText().toString();
            String textGender;

            if(TextUtils.isEmpty(textName)){
                Toast.makeText(RegisterActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                userName.setError("Name is required");
                userName.requestFocus();
            } else if(TextUtils.isEmpty(textEmail)){
                Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                userEmail.setError("Email is required");
                userEmail.requestFocus();
            } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                userEmail.setError("Valid email is required");
                userEmail.requestFocus();
            } else if(TextUtils.isEmpty(textDOB)){
                Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                userDOB.setError("Date of birth is required");
                userDOB.requestFocus();
            } else if(radioGroupGender.getCheckedRadioButtonId() == -1) {
                Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                radioBtnGender.setError("Gender is required");
                radioBtnGender.requestFocus();
            } else if(TextUtils.isEmpty(textMobile)){
                Toast.makeText(RegisterActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                userMobile.setError("Phone number is required");
                userMobile.requestFocus();
            } else if(TextUtils.isEmpty(textPassword)) {
                Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                userPassword.setError("Password is required");
                userPassword.requestFocus();
            } else if(textPassword.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password too weak", Toast.LENGTH_SHORT).show();
                userPassword.setError("Password must be at least 6 digits");
                userPassword.requestFocus();
            } else if(!textPassword.equals(textConfirmedPassword)) {
                Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                userConfirmedPassword.setError("Enter same password");
                userConfirmedPassword.requestFocus();
            } else {
                textGender = radioBtnGender.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                registerUser(textName, textEmail, textDOB, textGender, textMobile, textPassword);
            }
        });
    }
}
