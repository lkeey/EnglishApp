package com.example.englishapp.Authentication;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MainActivity;
import com.example.englishapp.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginUser";
    private EditText userEmail, userPassword;
    private Button btnLogin;
    private TextView forgotPassword, signUp, dialogText;
    private FirebaseAuth mAuth;
    private Dialog progressBar;
    private RelativeLayout signGoogle;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private Toolbar toolbar;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        init(view);

        mAuth = FirebaseAuth.getInstance();

        setListeners(view);

        return view;

    }

    private void setListeners(View view) {

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainAuthenticationActivity) getActivity()).setFragment(new LoginFragment());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    login();
                }
            }
        });

        signGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainAuthenticationActivity) getActivity()).setFragment(new LoginFragment());

            }
        });

        // for google authentication
        oneTapClient = Identity.getSignInClient(getActivity());
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
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

                        Toast.makeText(getActivity(), "API: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        progressBar.dismiss();
                        Log.i(TAG, e.getMessage());

                        Toast.makeText(getActivity(), "Something went wrong with getting data", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Something went wrong. Try later", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void init(View view) {
        userEmail = view.findViewById(R.id.editTextEmail);
        userPassword = view.findViewById(R.id.editTextPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        forgotPassword = view.findViewById(R.id.labelForgot);
        signUp = view.findViewById(R.id.labelAccount);
        signGoogle = view.findViewById(R.id.signGoogle);

        forgotPassword = view.findViewById(R.id.labelForgot);

        toolbar = view.findViewById(R.id.toolbar);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText("Signing in");

    }

    private void googleSignIn() {

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {

                            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(
                                    result.getPendingIntent().getIntentSender()
                            ).build();

                            activityResultLauncher.launch(intentSenderRequest);

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {

        progressBar.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.i(TAG, "Completed task " + task);

                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Google Sign In Was Successfully", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                                DataBase.createUserData(user.getEmail().trim(), user.getDisplayName(), "0" , "MAN", user.getPhoneNumber(), new CompleteListener() {
                                    @Override
                                    public void OnSuccess() {
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

                                    @Override
                                    public void OnFailure() {
                                        progressBar.dismiss();

                                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
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
                            progressBar.dismiss();

                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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

        mAuth.signInWithEmailAndPassword(userEmail.getText().toString().trim(), userPassword.getText().toString().trim())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(getActivity(), "Authentication was successfully",
                                    Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(getActivity(), "Something went wrong",
                                            Toast.LENGTH_SHORT).show();

                                    progressBar.dismiss();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getActivity(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            progressBar.dismiss();
                        }
                    }
                });
    }

}