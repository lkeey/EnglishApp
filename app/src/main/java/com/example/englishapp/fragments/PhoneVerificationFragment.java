package com.example.englishapp.fragments;

import static com.example.englishapp.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.activities.MainActivity;
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

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class PhoneVerificationFragment extends Fragment {

    private final static String TAG = "VerificationPhone";
    private Button btnVerify, btnSend;
    private TextView dialogText;
    private EditText userCode, userPhone;
    private FirebaseAuth mAuth;
    private String verificationCodeBySystem;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Dialog progressBar;

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
//            Toast.makeText(getActivity(), "Null Number", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void init(View view) {
        btnSend = view.findViewById(R.id.btnSendCode);
        btnVerify = view.findViewById(R.id.btnVerifyCode);
        userCode = view.findViewById(R.id.userCode);
        userPhone = view.findViewById(R.id.userPhone);

        // for beauty entering phone number
        MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        mask.setForbidInputWhenFilled(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(userPhone);

        btnVerify.setEnabled(false);
        userCode.setEnabled(false);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onCodeSent(@NonNull String codeSend, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(codeSend, forceResendingToken);

                progressBar.dismiss();

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
                progressBar.dismiss();

                Toast.makeText(getActivity(), "Authentication Failed - SMS quota exceeded", Toast.LENGTH_SHORT).show();

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

                Log.i(TAG, "Fail - " + e.getMessage());

            }
        };

        btnSend.setOnClickListener(v -> {

            Log.i(TAG, "Phone is " + userPhone.getText().toString().trim());
            Log.i(TAG, "Mask is " + formatWatcher);


            if (TextUtils.isEmpty(userPhone.getText().toString().trim())) {
                Toast.makeText(getActivity(), R.string.errorMobile, Toast.LENGTH_SHORT).show();
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

        btnVerify.setOnClickListener(v -> {

            if(!userCode.getText().toString().isEmpty()) {
//                Toast.makeText(getActivity(), "Checking - " + userCode.toString(), Toast.LENGTH_SHORT).show();

                verifyCode(userCode.getText().toString().trim());
            } else {
                Toast.makeText(getActivity(), "Write Code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerificationToUser(String phoneNumber) {

        Log.i(TAG, "Verify - " + phoneNumber);

        dialogText.setText(R.string.progressBarSendingOTP);
        progressBar.show();

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

    private void verifyCode(String userCode) {

        dialogText.setText(R.string.progressBarLogging);
        progressBar.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, userCode);

        Log.i(TAG, "Credential - " + credential);

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Sign In Was Successful", Toast.LENGTH_SHORT).show();

                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        Log.i(TAG, "New account");

                        DataBase.createUserData(null, null, null , null, userPhone.getText().toString(), null , new CompleteListener() {
                            @Override
                            public void OnSuccess() {
                                DataBase.loadData(new CompleteListener() {
                                    @Override
                                    public void OnSuccess() {
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
                    } else {
                        Log.i(TAG, "Old account");

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
                                progressBar.dismiss();

                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    Toast.makeText(getActivity(), "Something went wrong! Try later", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Fail" + task.getException().toString());
                    progressBar.dismiss();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Something went wrong! Try later", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFail" + e.getMessage());
                progressBar.dismiss();
            });
    }
}
