package com.example.englishapp.Authentication;

import static android.app.Activity.RESULT_OK;
import static com.example.englishapp.messaging.Constants.NAME_USER_PROFILE_IMG;
import static com.example.englishapp.messaging.Constants.PATH_PROFILE_IMG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.englishapp.MVP.CompleteListener;
import com.example.englishapp.MVP.DataBase;
import com.example.englishapp.MVP.FeedActivity;
import com.example.englishapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {

    private static final String TAG = "CreateUser";
    private String pathToImage, userDOB;
    private EditText userName, userEmail,
            userMobile, userPassword,
            userConfirmedPassword;
    private FirebaseAuth mAuth;
    private Dialog progressBar;
    private Button btnSignUp;
    private ImageView profileImg;
    private RadioGroup radioGroupGender;
    private RadioButton radioBtnGender;
    private TextView lblLogin, dialogText, textChooseDOB;
    private ActivityResultLauncher<Intent> pickImage;
    private DatePickerDialog datePicker;
    private StorageReference storageReference;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private Uri imgUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();

        init(view);

        ((MainAuthenticationActivity) getActivity()).setTitle(R.string.nameSignUp);

        setListeners(view);

        return view;

    }

    private void setListeners(View view) {
        lblLogin.setOnClickListener(v -> {
            ((MainAuthenticationActivity) getActivity()).setFragment(new LoginFragment());
        });

        textChooseDOB.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int yearData, int monthData, int dayOfMonthData) {

                    Calendar newDate = Calendar.getInstance();
                    newDate.set(yearData, monthData, dayOfMonthData);

                    userDOB = dayOfMonthData + "." + (monthData+1) + "." + yearData;

                    textChooseDOB.setText("Your Date Of Birth is " + userDOB);
                }
            }, year, month, day);

            datePicker.show();

            Log.i(TAG, "DOB - " + userDOB);

        });

        profileImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            pickImage.launch(intent);
        });

        btnSignUp.setOnClickListener(v -> {
            if (checkData(view)) {

                Log.i(TAG, "Data Checked");

                signUpUser(
                        userEmail.getText().toString(),
                        userPassword.getText().toString(),
                        userName.getText().toString(),
                        userDOB,
                        radioBtnGender.getText().toString(),
                        userMobile.getText().toString()
                );


            } else {
                Log.i(TAG, "Incorrect data");

            }
        });


    }

    private void signUpUser(String textEmail, String textPassword, String textName, String textDOB, String textGender, String textMobile)  {

        progressBar.show();

        mAuth.createUserWithEmailAndPassword(textEmail, textPassword)
            .addOnCompleteListener(getActivity(), (OnCompleteListener<AuthResult>) task -> {
                if (task.isSuccessful()) {

                    Toast.makeText(getActivity(), "Sign Up Was Successfully", Toast.LENGTH_SHORT).show();

                    DataBase.createUserData(
                        textEmail, textName,
                        textDOB, textGender, textMobile, "NULL_PATH",
                        new CompleteListener(){
                        @Override
                        public void OnSuccess() {
                            DataBase.loadData(new CompleteListener() {
                                @Override
                                public void OnSuccess() {

                                    if(imgUri != null) {
                                        Log.i(TAG, "HAVE IMAGE");

                                        uploadPicture(imgUri);
                                    }

                                    Log.i(TAG, "Successfully set data");

                                    progressBar.dismiss();

                                    Intent intent = new Intent(getActivity(), FeedActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                                @Override
                                public void OnFailure() {
                                    Log.i(TAG, "Can not set user data");

                                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    progressBar.dismiss();
                                }
                            });
                        }
                        @Override
                        public void OnFailure() {
                            Log.i(TAG, "Can not create user data");

                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressBar.dismiss();
                        }
                    });

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getActivity(), task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Sign Up Failed",
                            Toast.LENGTH_SHORT).show();
                    progressBar.dismiss();
                }
            });
    }
    
    private boolean checkData(View view) {
        boolean status = false;

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        radioBtnGender = view.findViewById(selectedGenderId);

        String textName = userName.getText().toString();
        String textEmail = userEmail.getText().toString();
        String textMobile = userMobile.getText().toString();
        String textPassword = userPassword.getText().toString();
        String textConfirmedPassword = userConfirmedPassword.getText().toString();

        //validate mobile phone
        String mobileRegex = "[6-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textName)) {
            Toast.makeText(getActivity(), R.string.errorName, Toast.LENGTH_SHORT).show();
            userName.setError(getResources().getString(R.string.requiredName));
            userName.requestFocus();

        } else if (TextUtils.isEmpty(textEmail)) {
            Toast.makeText(getActivity(), R.string.errorEmail, Toast.LENGTH_SHORT).show();
            userEmail.setError(getResources().getString(R.string.requiredEmail));
            userEmail.requestFocus();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            Toast.makeText(getActivity(), R.string.errorEmail, Toast.LENGTH_SHORT).show();
            userEmail.setError(getResources().getString(R.string.requiredEmail));
            userEmail.requestFocus();

        } else if (TextUtils.isEmpty(userDOB)) {
            Toast.makeText(getActivity(), R.string.errorDOB, Toast.LENGTH_SHORT).show();
            textChooseDOB.setError(getResources().getString(R.string.requiredDOB));
            textChooseDOB.requestFocus();

        } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), R.string.errorGender, Toast.LENGTH_SHORT).show();

            // Radio Button does not have this method:
            // radioBtnGender.setError(getResources().getString(R.string.requiredGender));
            // radioBtnGender.requestFocus();

        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(getActivity(), R.string.errorMobile, Toast.LENGTH_SHORT).show();
            userMobile.setError(getResources().getString(R.string.requiredMobile));
            userMobile.requestFocus();

        } else if (!mobileMatcher.find()) {
            Toast.makeText(getActivity(),  R.string.errorMobile, Toast.LENGTH_SHORT).show();
            userMobile.setError(getResources().getString(R.string.requiredMobile));
            userMobile.requestFocus();

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
        userName = view.findViewById(R.id.editTextName);
        userEmail = view.findViewById(R.id.editTextEmail);
        textChooseDOB = view.findViewById(R.id.textChooseDOB);
        userMobile = view.findViewById(R.id.editTextPhone);
        userPassword = view.findViewById(R.id.editTextPassword);
        userConfirmedPassword = view.findViewById(R.id.editTextConfirmedPassword);
        lblLogin = view.findViewById(R.id.labelHaveAccount);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        radioGroupGender = view.findViewById(R.id.groupGender);
        profileImg = view.findViewById(R.id.imageUser);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarCreating);

        storageReference = FirebaseStorage.getInstance().getReference(PATH_PROFILE_IMG);
        authProfile = FirebaseAuth.getInstance();

        pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() !=null) {
                        imgUri = result.getData().getData();

                        try {
                            InputStream inputStream = ((MainAuthenticationActivity) getActivity()).getContentResolver().openInputStream(imgUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            profileImg.setImageBitmap(bitmap);
//                            encodedImage = encodeImage(bitmap);

                        } catch (FileNotFoundException e) {
                            Log.i(TAG, e.getMessage());
                        }
                    }
                }
            }
        );

    }

    private void uploadPicture(Uri uriImg) {

        Log.i(TAG, "Create fileReference");

        Log.i(TAG, "UID - " + authProfile.getCurrentUser().getUid());

        Log.i(TAG, "EXTENSION - " + getFileExtension(uriImg));

        //Save image
        StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "/" + NAME_USER_PROFILE_IMG + "." + getFileExtension(uriImg));

        Log.i(TAG, fileReference.toString());

        //Upload image to Storage
        fileReference.putFile(uriImg).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.i(TAG, "PATH00" + uri.toString());

            firebaseUser = authProfile.getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri).build();

            firebaseUser.updateProfile(profileUpdates);

            pathToImage = uri.toString();

            Log.i(TAG, "PATH01 - " + pathToImage);

            DataBase.updateImage(pathToImage, new CompleteListener() {
                @Override
                public void OnSuccess() {
                    Log.i(TAG, "Photo successfully saved");
                }

                @Override
                public void OnFailure() {
                    Log.i(TAG, "Unable to save photo");

                }
            });

        })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Something went wrong - " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayInputStream);

        byte[] bytes = byteArrayInputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }
}