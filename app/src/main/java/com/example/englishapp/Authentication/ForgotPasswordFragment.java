package com.example.englishapp.Authentication;

import android.app.Dialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordFragment extends Fragment {


    private static final String TAG = "Reset Password";
    private Button btnResetPassword;
    private EditText userEmail;
    private FirebaseAuth authProfile;
    private TextView dialogText, lblLogin, lblSignUp;
    private Dialog progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        authProfile = FirebaseAuth.getInstance();

        init(view);

        setListeners();

        return view;
    }

    private void setListeners() {
        btnResetPassword.setOnClickListener(view -> {
            String textEmail = userEmail.getText().toString();

            if(TextUtils.isEmpty(textEmail)) {
                Toast.makeText(getActivity(), "Please enter your registered email", Toast.LENGTH_SHORT).show();
                userEmail.setError("Email is required");
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

        lblLogin.setOnClickListener(v -> {
            ((MainAuthenticationActivity) getActivity()).setFragment(new LoginFragment());
            ((MainAuthenticationActivity) getActivity()).setTitle(R.string.nameLogin);
        });

        lblSignUp.setOnClickListener(v -> {
            ((MainAuthenticationActivity) getActivity()).setFragment(new SignUpFragment());
            ((MainAuthenticationActivity) getActivity()).setTitle(R.string.nameSignUp);
        });
    }

    private void init(View view) {
        btnResetPassword = view.findViewById(R.id.btnPasswordReset);
        userEmail = view.findViewById(R.id.editTextEmail);
        lblLogin = view.findViewById(R.id.labelLogin);
        lblSignUp = view.findViewById(R.id.labelAccount);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarSending);

    }

    private void resetPassword(String textEmail) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(textEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(getActivity(), R.string.successSentEmail, Toast.LENGTH_SHORT).show();

            } else {
                try {
                    throw task.getException();
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