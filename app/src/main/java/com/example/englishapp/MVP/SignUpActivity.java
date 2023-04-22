package com.example.englishapp.MVP;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.englishapp.R;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "Create User";
    private EditText userName, userEmail, userDOB,
            userMobile, userPassword,
            userConfirmedPassword;
    private Dialog progressBar;
    private Button btnSignUp;
    private RadioGroup radioGroupGender;
    private RadioButton radioBtnGender;
    private DatePickerDialog picker;
    private TextView lblLogin, dialogText;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.editTextName);
        userEmail = findViewById(R.id.editTextEmail);
        userDOB = findViewById(R.id.editTextDOB);
        userMobile = findViewById(R.id.editTextPhone);
        userPassword = findViewById(R.id.editTextPassword);
        userConfirmedPassword = findViewById(R.id.editTextConfirmedPassword);
        toolbar = findViewById(R.id.toolbar);
        lblLogin = findViewById(R.id.labelHaveAccount);
        btnSignUp = findViewById(R.id.btnSignUp);
        radioGroupGender = findViewById(R.id.groupGender);

        radioGroupGender.clearCheck();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_btn_back);
        getSupportActionBar().setTitle("Sign Up");

        progressBar = new Dialog(SignUpActivity.this);
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText("Creating");


        lblLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {

                    progressBar.show();

                    DataBase.createUserData(
                            userEmail.getText().toString(),
                            userName.getText().toString(),
                            userDOB.getText().toString(),
                            radioBtnGender.getText().toString(),
                            userMobile.getText().toString(),
                            "NULL_PATH",

                            new CompleteListener() {
                                @Override
                                public void OnSuccess() {
                                    progressBar.dismiss();
                                }

                                @Override
                                public void OnFailure() {
                                    progressBar.dismiss();
                                }
                            }
                    );
                }
            }
        });

        userDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //DatePicker dialog
                picker = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        userDOB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });
    }

    private boolean checkData() {
        boolean status = false;

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        radioBtnGender = SignUpActivity.this.findViewById(selectedGenderId);

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
            Toast.makeText(SignUpActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            userName.setError("Name is required");
            userName.requestFocus();
        } else if (TextUtils.isEmpty(textEmail)) {
            Toast.makeText(SignUpActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            userEmail.setError("Email is required");
            userEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            Toast.makeText(SignUpActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
            userEmail.setError("Valid email is required");
            userEmail.requestFocus();
        } else if (TextUtils.isEmpty(textDOB)) {
            Toast.makeText(SignUpActivity.this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
            userDOB.setError("Date of birth is required");
            userDOB.requestFocus();
        } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(SignUpActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            radioBtnGender.setError("Gender is required");
            radioBtnGender.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(SignUpActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            userMobile.setError("Phone number is required");
            userMobile.requestFocus();
        } else if (!mobileMatcher.find()) {
            Toast.makeText(SignUpActivity.this, "Please re-enter your phone number", Toast.LENGTH_SHORT).show();
            userMobile.setError("Phone number is not valid");
            userMobile.requestFocus();
        } else if (TextUtils.isEmpty(textPassword)) {
            Toast.makeText(SignUpActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            userPassword.setError("Password is required");
            userPassword.requestFocus();
        } else if (textPassword.length() < 6) {
            Toast.makeText(SignUpActivity.this, "Password too weak", Toast.LENGTH_SHORT).show();
            userPassword.setError("Password must be at least 6 digits");
            userPassword.requestFocus();
        } else if (!textPassword.equals(textConfirmedPassword)) {
            Toast.makeText(SignUpActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
            userConfirmedPassword.setError("Enter same password");
            userConfirmedPassword.requestFocus();
        } else {
            status = true;
        }

        return status;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            SignUpActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}