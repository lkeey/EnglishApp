package com.example.englishapp.Authentication;

import android.os.Bundle;
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

public class PhoneVerificationFragment extends Fragment {

    private final static String TAG = "Verification Phone";
    private Button btnVerify;
    private EditText userPhone;
    private FirebaseAuth mAuth;
    private String verificationCodeBySystem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_phone_verification, container, false);

        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String receiveInfo = bundle.getString("phone");

            Toast.makeText(getActivity(), receiveInfo, Toast.LENGTH_SHORT).show();

            sendVerificationToUser(receiveInfo);
        }


        return view;
    }

    private void sendVerificationToUser(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)// OnVerificationStateChangedCallbacks
                        .setActivity(getActivity())
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        Log.i(TAG, "Begin verifying");
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String codeSend, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(codeSend, forceResendingToken);

            Log.i(TAG, "Code Sent");

            verificationCodeBySystem = codeSend;

            Log.i(TAG, "Code - " + codeSend);
            verifyCode("202020");
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //Log.i(TAG, "Verification Completed");

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
            Log.i(TAG, "Fail - " + e.getMessage());

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Log.i(TAG, "Invalid request");

            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.i(TAG, "The SMS quota for the project has been exceeded");

            } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                Log.i(TAG, "reCAPTCHA verification attempted with null Activity");
            }

            //Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String userCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, userCode);

        Log.i(TAG, "Credential - " + credential.toString());

        //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

    }
}
