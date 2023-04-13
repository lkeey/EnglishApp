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

        mAuth.setLanguageCode("fr");
        // To apply the default app language instead of explicitly setting it.
        // auth.useAppLanguage();

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
                        .setActivity(getActivity()) // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:" + credential);

//            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
//            mVerificationId = verificationId;
//            mResendToken = token;
        }
    };


//    private void sendVerificationToUser(String phoneNumber) {
//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber(phoneNumber)       // Phone number to verify
//                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                        // If no activity is passed, reCAPTCHA verification can not be used.
//                        .setCallbacks(mCallbacks)// OnVerificationStateChangedCallbacks
//                        .setActivity(getActivity())
//                        .build();
//
//        PhoneAuthProvider.verifyPhoneNumber(options);
//
//        Log.i(TAG, "Begin verifying");
//    }
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        @Override
//        public void onCodeSent(@NonNull String codeSend, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(codeSend, forceResendingToken);
//
//            Log.i(TAG, "Code Sent");
//
//            verificationCodeBySystem = codeSend;
//
//            Log.i(TAG, "Code - " + codeSend);
////            verifyCode("202020");
//        }
//
//        @Override
//        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            //Log.i(TAG, "Verification Completed");
//
//            String code = phoneAuthCredential.getSmsCode();
//            if (code != null) {
//                verifyCode(code);
//
//                Log.i(TAG, "Verification Completed");
//
//            } else {
//                Log.i(TAG, "Empty Data");
//            }
//        }
//
//        @Override
//        public void onVerificationFailed(@NonNull FirebaseException e) {
//            Log.i(TAG, "Fail - " + e.getMessage());
//
//            if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                Log.i(TAG, "Invalid request");
//                Toast.makeText(getActivity(), "Invalid request", Toast.LENGTH_SHORT).show();
//
//            } else if (e instanceof FirebaseTooManyRequestsException) {
//                Log.i(TAG, "The SMS quota for the project has been exceeded");
//                Toast.makeText(getActivity(), "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
//
//            } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
//                // reCAPTCHA verification attempted with null Activity
//                Log.i(TAG, "reCAPTCHA verification attempted with null Activity");
//                Toast.makeText(getActivity(), "reCAPTCHA verification attempted with null Activity", Toast.LENGTH_SHORT).show();
//
//            }
//
//            Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    private void verifyCode(String userCode) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, userCode);
//
//        Log.i(TAG, "Credential - " + credential.toString());
//
//        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
//
//    }
}
