package com.example.englishapp.presentation.fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.englishapp.data.database.Constants.KEY_GOOGLE_WEB_CLIENT_ID;
import static com.example.englishapp.data.database.Constants.SHOW_FRAGMENT_DIALOG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.presentation.activities.MainAuthenticationActivity;
import com.example.englishapp.domain.interfaces.AuthenticationListener;
import com.example.englishapp.domain.repositories.LoginRepository;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginUser";
    private EditText userEmail, userPassword;
    private TextView forgotPassword, signUp;
    private Dialog progressBar;
    private RelativeLayout signGoogle, signPhone, layoutLogin;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private LoginRepository repository;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        init(view);

        repository = new LoginRepository();

        requireActivity().setTitle(R.string.nameLogin);

        setListeners();

        return view;

    }


    private void init(View view) {
        userEmail = view.findViewById(R.id.editTextEmail);
        userPassword = view.findViewById(R.id.editTextPassword);
        layoutLogin = view.findViewById(R.id.layoutLogin);
        forgotPassword = view.findViewById(R.id.labelForgot);
        signUp = view.findViewById(R.id.labelAccount);
        signGoogle = view.findViewById(R.id.signGoogle);
        signPhone = view.findViewById(R.id.signPhone);

        forgotPassword = view.findViewById(R.id.labelForgot);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarLogging);

    }

    private void setListeners() {

        signUp.setOnClickListener(v -> ((MainAuthenticationActivity) requireActivity()).setFragment(new SignUpFragment()));

        layoutLogin.setOnClickListener(v -> {
            if (validateData()) {
                Log.i(TAG, "begin logging");

                login();
            }
        });

        signGoogle.setOnClickListener(v -> googleSignIn());

        signPhone.setOnClickListener(v -> ((MainAuthenticationActivity) requireActivity()).setFragment(new PhoneVerificationFragment()));

        forgotPassword.setOnClickListener(v -> ((MainAuthenticationActivity) requireActivity()).setFragment(new ForgotPasswordFragment()));

        Log.i(TAG, "default_web_client_id - " + KEY_GOOGLE_WEB_CLIENT_ID);

        // for google authentication
        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
//                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setServerClientId(KEY_GOOGLE_WEB_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {

                try {

                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String idToken = credential.getGoogleIdToken();

                    if (idToken != null) {
                        String email = credential.getId();
                        Log.i(TAG, "EMAIL - " + email);

                        firebaseAuthWithGoogle(idToken);
                    }

                } catch (ApiException e) {
                    progressBar.dismiss();
                    Log.i(TAG, "api - " + e.getMessage());

                    Toast.makeText(getActivity(), "API: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    progressBar.dismiss();
                    Log.i(TAG, "Exception - " + e.getMessage());

                    Toast.makeText(getActivity(), "Something went wrong with getting data", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), "Something went wrong. Try later", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "fail - result not ok - " + result.getData());
            }
        });
    }


    private void googleSignIn() {

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity(), result -> {
                try {

                    Log.i(TAG, "begin sign in google");

                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(
                            result.getPendingIntent().getIntentSender()
                    ).build();

                    activityResultLauncher.launch(intentSenderRequest);

                } catch (Exception e) {

                    Toast.makeText(getActivity(), "Can not sign up", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "exception - " + e.getMessage());

                }
            })
            .addOnFailureListener(requireActivity(), e -> {
                Toast.makeText(getActivity(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "fail - " + e.getMessage());
            });
    }

    private void firebaseAuthWithGoogle(String idToken) {

        progressBar.show();

        repository.firebaseAuthWithGoogle(idToken, new AuthenticationListener() {
            @Override
            public void createNewAccount() {
                progressBar.dismiss();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(SHOW_FRAGMENT_DIALOG, true);
                startActivity(intent);
                Log.i(TAG, "STARTED");

                requireActivity().finish();
            }

            @Override
            public void logInAccount() {
                progressBar.dismiss();

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }

            @Override
            public void onFailure() {
                progressBar.dismiss();

                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateData() {
        boolean status = true;

        if (userEmail.getText().toString().isEmpty()) {
            userEmail.setError("Enter email!");
            status = false;
        } else if (userPassword.getText().toString().isEmpty()) {
            userPassword.setError("Enter Password!");
            status = false;
        }

        return status;
    }

    private void login() {
        progressBar.show();

        repository.login(userEmail.getText().toString().trim(), userPassword.getText().toString().trim(), new AuthenticationListener() {
            @Override
            public void createNewAccount() {

            }

            @Override
            public void logInAccount() {
                progressBar.dismiss();

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();

            }

            @Override
            public void onFailure() {
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

                progressBar.dismiss();
            }
        });
    }

}