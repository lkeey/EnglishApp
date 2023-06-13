package com.example.englishapp.presentation.fragments;

import android.app.Dialog;
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
import com.example.englishapp.presentation.activities.MainAuthenticationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class ForgotPasswordFragment extends Fragment {

    private static final String TAG = "Reset Password";
    private Button btnResetPassword;
    private EditText userEmail;
    private FirebaseAuth authProfile;
    private TextView lblLogin, lblSignUp;
    private Dialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        authProfile = FirebaseAuth.getInstance();

        init(view);

        requireActivity().setTitle(R.string.nameForgot);

        setListeners();

        return view;
    }

    private void setListeners() {
        btnResetPassword.setOnClickListener(view -> {
            String textEmail = userEmail.getText().toString();

            if(TextUtils.isEmpty(textEmail)) {
                Toast.makeText(getActivity(), getString(R.string.please_enter_your_registered_email), Toast.LENGTH_SHORT).show();
                userEmail.setError(getString(R.string.email_is_required));
                userEmail.requestFocus();
            } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                Toast.makeText(getActivity(), R.string.errorEmail, Toast.LENGTH_SHORT).show();
                userEmail.setError(getResources().getString(R.string.requiredEmail));
                userEmail.requestFocus();
            } else {
                progressBar.show();
                resetPassword(textEmail);
            }
        });

        lblLogin.setOnClickListener(v -> ((MainAuthenticationActivity) requireActivity()).setFragment(new LoginFragment()));

        lblSignUp.setOnClickListener(v -> ((MainAuthenticationActivity) requireActivity()).setFragment(new SignUpFragment()));
    }

    private void init(View view) {
        btnResetPassword = view.findViewById(R.id.btnPasswordReset);
        userEmail = view.findViewById(R.id.editTextEmail);
        lblLogin = view.findViewById(R.id.labelLogin);
        lblSignUp = view.findViewById(R.id.labelAccount);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarSending);

    }

    private void resetPassword(String textEmail) {
        authProfile = FirebaseAuth.getInstance();

        authProfile.sendPasswordResetEmail(textEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()){

                Log.i(TAG, "Email was sent to - " + textEmail);

                Toast.makeText(getActivity(), R.string.successSentEmail, Toast.LENGTH_SHORT).show();

            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    userEmail.setError(getResources().getString(R.string.failExist));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.dismiss();
        });
    }
}
