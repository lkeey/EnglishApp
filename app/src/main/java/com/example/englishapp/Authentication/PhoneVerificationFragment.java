package com.example.englishapp.Authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneVerificationFragment extends Fragment {

    private final static String TAG = "Verification Phone";
    private Button btnVerify, btnSend;
    private EditText userCode, userPhone;
    private FirebaseAuth mAuth;
    private String verificationCodeBySystem;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_phone_verification, container, false);

        mAuth = FirebaseAuth.getInstance();

        // To apply the default app language instead of explicitly setting it.
        mAuth.setLanguageCode("en");

        init(view);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String receiveInfo = bundle.getString("phone");

            Toast.makeText(getActivity(), "Phone - " + receiveInfo, Toast.LENGTH_SHORT).show();

            userPhone.setText(receiveInfo);

        } else {
            Toast.makeText(getActivity(), "Null Number", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void init(View view) {
        btnSend = view.findViewById(R.id.btnSendCode);
        btnVerify = view.findViewById(R.id.btnVerifyCode);
        userCode = view.findViewById(R.id.userCode);
        userPhone = view.findViewById(R.id.userPhone);

        btnVerify.setEnabled(false);
        userCode.setEnabled(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onCodeSent(@NonNull String codeSend, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(codeSend, forceResendingToken);

                Toast.makeText(getActivity(), "Code has successfully sent", Toast.LENGTH_SHORT).show();

                Log.i(TAG, "Code Sent");

                verificationCodeBySystem = codeSend;

                Log.i(TAG, "Code - " + codeSend);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.i(TAG, "Verification Completed");

                String code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    verifyCode(code);

                    Log.i(TAG, "Verification Completed");

                } else {
                    Log.i(TAG, "Empty Data");
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.i(TAG, "Fail");

                Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.i(TAG, "Invalid request");
                    Toast.makeText(getActivity(), "Invalid request", Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.i(TAG, "The SMS quota for the project has been exceeded");
                    Toast.makeText(getActivity(), "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                    Log.i(TAG, "reCAPTCHA verification attempted with null Activity");
                    Toast.makeText(getActivity(), "reCAPTCHA verification attempted with null Activity", Toast.LENGTH_SHORT).show();

                }
            }
        };

        btnSend.setOnClickListener(v -> {
            String mobileRegex = "[6-9][0-9]{9}";
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            Matcher mobileMatcher = mobilePattern.matcher(userPhone.getText().toString().trim());

            if (TextUtils.isEmpty(userPhone.getText().toString().trim())) {
                Toast.makeText(getActivity(), R.string.errorMobile, Toast.LENGTH_SHORT).show();
                userPhone.setError(getResources().getString(R.string.requiredMobile));
                userPhone.requestFocus();

            } else if (!mobileMatcher.find()) {
                Toast.makeText(getActivity(),  R.string.errorMobile, Toast.LENGTH_SHORT).show();
                userPhone.setError(getResources().getString(R.string.requiredMobile));
                userPhone.requestFocus();

            } else {
                btnVerify.setEnabled(true);
                userCode.setEnabled(true);
                userPhone.setEnabled(false);
                btnSend.setEnabled(false);

                sendVerificationToUser(userPhone.getText().toString().trim());
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!userCode.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Checking - " + userCode.toString(), Toast.LENGTH_SHORT).show();

                    verifyCode(userCode.getText().toString().trim());
                } else {
                    Toast.makeText(getActivity(), "Write Code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationToUser(String phoneNumber) {

        Log.i(TAG, "Verify - " + phoneNumber);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)// OnVerificationStateChangedCallbacks
                        .setActivity((MainAuthenticationActivity) getActivity())
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        Log.i(TAG, "Begin verifying");
    }

    private void verifyCode(String userCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, userCode);

        Log.i(TAG, "Credential - " + credential.toString());

        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

    }
}
