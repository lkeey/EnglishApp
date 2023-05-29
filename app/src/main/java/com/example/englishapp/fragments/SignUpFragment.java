package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.englishapp.activities.MainAuthenticationActivity;
import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment {

    private static final String TAG = "CreateUser";
    private EditText userEmail, userPassword,
            userConfirmedPassword;
    private FirebaseAuth mAuth;
    private Dialog progressBar;
    private Button btnSignUp;
    private TextView lblLogin, dialogText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();

        init(view);

        getActivity().setTitle(R.string.nameSignUp);

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

                signUpUser(
                        userEmail.getText().toString(),
                        userPassword.getText().toString()
                );


            } else {
                Log.i(TAG, "Incorrect data");

            }
        });


    }

    private void signUpUser(String textEmail, String textPassword)  {

        progressBar.show();

        mAuth.createUserWithEmailAndPassword(textEmail, textPassword)
            .addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {

                    DataBase.createUserData(textEmail, null, null, null, null, null, new CompleteListener() {
                        @Override
                        public void OnSuccess() {
                            DataBase.loadData(new CompleteListener() {
                                @Override
                                public void OnSuccess() {
                                    Toast.makeText(getActivity(), "Sign Up Was Successfully", Toast.LENGTH_SHORT).show();

                                    progressBar.dismiss();

                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra(SHOW_FRAGMENT_DIALOG, true);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                                @Override
                                public void OnFailure() {
                                    progressBar.dismiss();

                                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void OnFailure() {
                            progressBar.dismiss();

                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });



                    progressBar.dismiss();

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

        String textEmail = userEmail.getText().toString();
        String textPassword = userPassword.getText().toString();
        String textConfirmedPassword = userConfirmedPassword.getText().toString();

        if (TextUtils.isEmpty(textEmail)) {
            Toast.makeText(getActivity(), R.string.errorEmail, Toast.LENGTH_SHORT).show();
            userEmail.setError(getResources().getString(R.string.requiredEmail));
            userEmail.requestFocus();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            Toast.makeText(getActivity(), R.string.errorEmail, Toast.LENGTH_SHORT).show();
            userEmail.setError(getResources().getString(R.string.requiredEmail));
            userEmail.requestFocus();

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
        userEmail = view.findViewById(R.id.editTextEmail);
        userPassword = view.findViewById(R.id.editTextPassword);
        userConfirmedPassword = view.findViewById(R.id.editTextConfirmedPassword);
        lblLogin = view.findViewById(R.id.labelHaveAccount);
        btnSignUp = view.findViewById(R.id.btnSignUp);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarCreating);

    }

}