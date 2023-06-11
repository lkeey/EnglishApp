package com.example.englishapp.presentation.fragments;

import static com.example.englishapp.data.database.Constants.SHOW_FRAGMENT_DIALOG;

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

import com.example.englishapp.R;
import com.example.englishapp.domain.interfaces.AuthenticationListener;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.presentation.activities.MainAuthenticationActivity;
import com.example.englishapp.domain.repositories.SignupRepository;

public class SignUpFragment extends Fragment {

    private static final String TAG = "CreateUser";
    private EditText userEmail, userPassword,
            userConfirmedPassword;
    private Dialog progressBar;
    private Button btnSignUp;
    private TextView lblLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        init(view);

        requireActivity().setTitle(R.string.nameSignUp);

        setListeners();

        return view;

    }

    private void setListeners() {
        lblLogin.setOnClickListener(v -> ((MainAuthenticationActivity) requireActivity()).setFragment(new LoginFragment()));

        btnSignUp.setOnClickListener(v -> {
            if (checkData()) {

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

        SignupRepository repository = new SignupRepository();
        repository.signUpUser(textEmail, textPassword, new AuthenticationListener() {
            @Override
            public void createNewAccount() {
                progressBar.dismiss();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(SHOW_FRAGMENT_DIALOG, true);
                startActivity(intent);
                requireActivity().finish();
            }

            @Override
            public void logInAccount() {

            }

            @Override
            public void onFailure() {
                progressBar.dismiss();

                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private boolean checkData() {
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

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarCreating);

    }

}