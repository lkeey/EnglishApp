package com.example.englishapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;
import com.example.englishapp.models.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName, userEmail, userDOB,
            userMobile, userPassword,
            userConfirmedPassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupGender;
    private RadioButton radioBtnGender;
    private DatePickerDialog picker;
    private static final String TAG = "RegisterActivity";

    private void registerUser(String textName, String textEmail, String textDOB, String textGender, String textMobile, String textPassword){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Update display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter the data to FireBase
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(
                            textDOB, textGender, textMobile
                    );

                    //Extracting User reference from Database for "Registered Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterActivity.this, "I'm here666", Toast.LENGTH_SHORT).show();

                            if (task.isSuccessful()) {
                                //Send verification email
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "User registered successfully! Please verify your email", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "User registered failed! Please try again", Toast.LENGTH_SHORT).show();
                            }

                            Toast.makeText(RegisterActivity.this, "I'm here4", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    Toast.makeText(RegisterActivity.this, "I'm here3", Toast.LENGTH_SHORT).show();


                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        userPassword.setError("Your password is too weak");
                        userPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        userEmail.setError("Your email is invalid or already in use");
                        userEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        userEmail.setError("User is already registered with this email");
                        userEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
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

        //Setting up DatePicker on EditText
        userDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //DatePicker dialog
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        userDOB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                radioBtnGender = RegisterActivity.this.findViewById(selectedGenderId);

                String textName = userName.getText().toString();
                String textEmail = userEmail.getText().toString();
                String textDOB = userDOB.getText().toString();
                String textMobile = userMobile.getText().toString();
                String textPassword = userPassword.getText().toString();
                String textConfirmedPassword = userConfirmedPassword.getText().toString();
                String textGender;

                //validate mobile phone
                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if (TextUtils.isEmpty(textName)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    userName.setError("Name is required");
                    userName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    userEmail.setError("Email is required");
                    userEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    userEmail.setError("Valid email is required");
                    userEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDOB)) {
                    Toast.makeText(RegisterActivity.this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
                    userDOB.setError("Date of birth is required");
                    userDOB.requestFocus();
                } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    radioBtnGender.setError("Gender is required");
                    radioBtnGender.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    userMobile.setError("Phone number is required");
                    userMobile.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your phone number", Toast.LENGTH_SHORT).show();
                    userMobile.setError("Phone number is not valid");
                    userMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    userPassword.setError("Password is required");
                    userPassword.requestFocus();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password too weak", Toast.LENGTH_SHORT).show();
                    userPassword.setError("Password must be at least 6 digits");
                    userPassword.requestFocus();
                } else if (!textPassword.equals(textConfirmedPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    userConfirmedPassword.setError("Enter same password");
                    userConfirmedPassword.requestFocus();
                } else {
                    textGender = radioBtnGender.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    RegisterActivity.this.registerUser(textName, textEmail, textDOB, textGender, textMobile, textPassword);
                }
            }
        });
    }
}
