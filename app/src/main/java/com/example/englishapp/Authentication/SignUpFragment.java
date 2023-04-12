package com.example.englishapp.Authentication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MainActivity;
import com.example.englishapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {

    private static final String TAG = "Create User";
    private EditText userName, userEmail, userDOB,
            userMobile, userPassword,
            userConfirmedPassword;
    private FirebaseAuth mAuth;
    private Dialog progressBar;
    private Button btnSignUp;
    private RadioGroup radioGroupGender;
    private RadioButton radioBtnGender;
    private DatePickerDialog picker;
    private TextView lblLogin, dialogText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();

        init(view);

        ((MainAuthenticationActivity) getActivity()).setTitle(R.string.nameSignUp);

        setListeners(view);

        return view;

    }

    private void setListeners(View view) {
        lblLogin.setOnClickListener(v -> {
            ((MainAuthenticationActivity) getActivity()).setFragment(new LoginFragment());
        });

        btnSignUp.setOnClickListener(v -> {
            if (checkData(view)) {

                Log.i(TAG, "Data Checked");

                PhoneVerificationFragment fragment = new PhoneVerificationFragment();
                Bundle bundle = new Bundle();
                // put data into fragment
                bundle.putString("phone", userMobile.getText().toString());
                fragment.setArguments(bundle);

                ((MainAuthenticationActivity) getActivity()).setFragment(fragment);

//                progressBar.show();
//
//                signUpUser(
//                        userEmail.getText().toString(),
//                        userPassword.getText().toString(),
//                        userName.getText().toString(),
//                        userDOB.getText().toString(),
//                        radioBtnGender.getText().toString(),
//                        userMobile.getText().toString()
//                );
            } else {
                Log.i(TAG, "Incorrect data");

            }
        });

        userDOB.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            //DatePicker dialog
            picker = new DatePickerDialog(getActivity(), (datePicker, year1, month1, dayOfMonth) -> userDOB.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            picker.show();
        });

    }

    private void signUpUser(String textEmail, String textPassword, String textName, String textDOB, String textGender, String textMobile)  {

        progressBar.show();

        mAuth.createUserWithEmailAndPassword(textEmail, textPassword)
            .addOnCompleteListener(getActivity(), (OnCompleteListener<AuthResult>) task -> {
                if (task.isSuccessful()) {

                    Toast.makeText(getActivity(), "Sign Up Was Successfully", Toast.LENGTH_SHORT).show();

                    DataBase.createUserData(
                            textEmail, textName,
                            textDOB, textGender, textMobile,
                            new CompleteListener(){
                        @Override
                        public void OnSuccess() {

                            DataBase.loadData(new CompleteListener() {
                                @Override
                                public void OnSuccess() {
                                    progressBar.dismiss();

                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                                @Override
                                public void OnFailure() {
                                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    progressBar.dismiss();
                                }
                            });
                        }
                        @Override
                        public void OnFailure() {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressBar.dismiss();
                        }
                    });


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getActivity(), task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Sign Up Failed",
                            Toast.LENGTH_SHORT).show();
                    progressBar.dismiss();
                }
            });
    }
    
    private boolean checkData(View view) {
        boolean status = false;

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        radioBtnGender = view.findViewById(selectedGenderId);

        String textName = userName.getText().toString();
        String textEmail = userEmail.getText().toString();
        String textDOB = userDOB.getText().toString();
        String textMobile = userMobile.getText().toString();
        String textPassword = userPassword.getText().toString();
        String textConfirmedPassword = userConfirmedPassword.getText().toString();

        //validate mobile phone
        String mobileRegex = "[6-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textName)) {
            Toast.makeText(getActivity(), R.string.errorName, Toast.LENGTH_SHORT).show();
            userName.setError(getResources().getString(R.string.requiredName));
            userName.requestFocus();

        } else if (TextUtils.isEmpty(textEmail)) {
            Toast.makeText(getActivity(), R.string.errorEmail, Toast.LENGTH_SHORT).show();
            userEmail.setError(getResources().getString(R.string.requiredEmail));
            userEmail.requestFocus();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            Toast.makeText(getActivity(), R.string.errorEmail, Toast.LENGTH_SHORT).show();
            userEmail.setError(getResources().getString(R.string.requiredEmail));
            userEmail.requestFocus();

        } else if (TextUtils.isEmpty(textDOB)) {
            Toast.makeText(getActivity(), R.string.errorDOB, Toast.LENGTH_SHORT).show();
            userDOB.setError(getResources().getString(R.string.requiredDOB));
            userDOB.requestFocus();

        } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), R.string.errorGender, Toast.LENGTH_SHORT).show();

            // Radio Button does not have this method:
            // radioBtnGender.setError(getResources().getString(R.string.requiredGender));
            // radioBtnGender.requestFocus();

        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(getActivity(), R.string.errorMobile, Toast.LENGTH_SHORT).show();
            userMobile.setError(getResources().getString(R.string.requiredMobile));
            userMobile.requestFocus();

        } else if (!mobileMatcher.find()) {
            Toast.makeText(getActivity(),  R.string.errorMobile, Toast.LENGTH_SHORT).show();
            userMobile.setError(getResources().getString(R.string.requiredMobile));
            userMobile.requestFocus();

        } else if (TextUtils.isEmpty(textPassword)) {
            Toast.makeText(getActivity(), R.string.errorPassword, Toast.LENGTH_SHORT).show();
            userPassword.setError(getResources().getString(R.string.requiredPassword));
            userPassword.requestFocus();

        } else if (textPassword.length() < 6) {
            Toast.makeText(getActivity(), R.string.errorPassword, Toast.LENGTH_SHORT).show();
            userPassword.setError(getResources().getString(R.string.requiredPassword));
            userPassword.requestFocus();

        } else if (!textPassword.equals(textConfirmedPassword)) {
            Toast.makeText(getActivity(), R.string.errorConfirmedPassword, Toast.LENGTH_SHORT).show();
            userConfirmedPassword.setError(getResources().getString(R.string.requiredConfirmedPassword));
            userConfirmedPassword.requestFocus();

        } else {
            status = true;
        }

        return status;
    }

    private void init(View view) {
        userName = view.findViewById(R.id.editTextName);
        userEmail = view.findViewById(R.id.editTextEmail);
        userDOB = view.findViewById(R.id.editTextDOB);
        userMobile = view.findViewById(R.id.editTextPhone);
        userPassword = view.findViewById(R.id.editTextPassword);
        userConfirmedPassword = view.findViewById(R.id.editTextConfirmedPassword);
        lblLogin = view.findViewById(R.id.labelHaveAccount);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        radioGroupGender = view.findViewById(R.id.groupGender);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarCreating);
    }
}