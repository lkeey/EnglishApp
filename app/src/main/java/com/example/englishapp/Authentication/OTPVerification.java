package com.example.englishapp.Authentication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class OTPVerification extends AppCompatActivity {
    private final static String TAG = "Verification Phone";
    private FirebaseAuth mAuth;
    private String verificationCodeBySystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        mAuth = FirebaseAuth.getInstance();

        sendVerificationToUser("+79035971498");

    }

    private void sendVerificationToUser(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)// OnVerificationStateChangedCallbacks
                        .setActivity(this)
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
            verifyCode(codeSend);
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
                Toast.makeText(OTPVerification.this, "Invalid request", Toast.LENGTH_SHORT).show();

            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.i(TAG, "The SMS quota for the project has been exceeded");
                Toast.makeText(OTPVerification.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();

            } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                Log.i(TAG, "reCAPTCHA verification attempted with null Activity");
                Toast.makeText(OTPVerification.this, "reCAPTCHA verification attempted with null Activity", Toast.LENGTH_SHORT).show();

            }

            Toast.makeText(OTPVerification.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String userCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, userCode);

        Log.i(TAG, "Credential - " + credential.toString());

        Toast.makeText(OTPVerification.this, "Success", Toast.LENGTH_SHORT).show();

    }

}