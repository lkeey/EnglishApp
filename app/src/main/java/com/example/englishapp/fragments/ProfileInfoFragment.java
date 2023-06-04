package com.example.englishapp.fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.englishapp.database.Constants.KEY_ADD_SCORE;
import static com.example.englishapp.database.Constants.KEY_DOB;
import static com.example.englishapp.database.Constants.KEY_EMAIL;
import static com.example.englishapp.database.Constants.KEY_GENDER;
import static com.example.englishapp.database.Constants.KEY_LANGUAGE_CODE;
import static com.example.englishapp.database.Constants.KEY_NAME;
import static com.example.englishapp.database.Constants.KEY_SCORE;
import static com.example.englishapp.database.Constants.NAME_USER_PROFILE_IMG;
import static com.example.englishapp.database.Constants.PATH_PROFILE_IMG;
import static com.example.englishapp.database.DataBase.USER_MODEL;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.interfaces.CompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class ProfileInfoFragment extends Fragment {

    private static final String TAG = "UpdateProfileInfo";
    private String pathToImage, userDOB;
    private EditText userName, userEmail;
    private Dialog progressBar;
    private Button btnUpdate;
    private ImageView profileImg;
    private RadioGroup radioGroupGender;
    private RadioButton radioBtnGender;
    private TextView dialogText, textChooseDOB;
    private ActivityResultLauncher<Intent> pickImage;
    private DatePickerDialog datePicker;
    private StorageReference storageReference;
    private FirebaseAuth authProfile;
    private Spinner spinnerLanguage;
    private FirebaseUser firebaseUser;
    private Uri imgUri;
    private String languageCode;
    private boolean isAddingScore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        init(view);

        setPreviousData(view);

        try {
            getActivity().setTitle(R.string.nameLogin);
        } catch (Exception e) {
            Log.i(TAG, "e - " + e.getMessage());
        }

        setListeners(view);

        receiveData();

        return view;
    }

    private void receiveData() {

        Bundle bundle = getArguments();

        if (bundle != null) {
            isAddingScore = bundle.getBoolean(KEY_ADD_SCORE, false);
        }
    }

    private void init(View view) {
        userName = view.findViewById(R.id.editTextName);
        userEmail = view.findViewById(R.id.editTextEmail);
        textChooseDOB = view.findViewById(R.id.textChooseDOB);
        btnUpdate = view.findViewById(R.id.btnSignUp);
        radioGroupGender = view.findViewById(R.id.groupGender);
        profileImg = view.findViewById(R.id.imageUser);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarCreating);

        storageReference = FirebaseStorage.getInstance().getReference(PATH_PROFILE_IMG);
        authProfile = FirebaseAuth.getInstance();

        pickImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            imgUri = result.getData().getData();

                            try {
                                Log.i(TAG, "set bitmap");

                                InputStream inputStream = getActivity().getContentResolver().openInputStream(imgUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                profileImg.setImageBitmap(bitmap);

                                Log.i(TAG, "set bitmap 2");

//                            encodedImage = encodeImage(bitmap);

                            } catch (FileNotFoundException e) {
                                Log.i(TAG, e.getMessage());
                            }
                        }
                    }
                }
        );

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, TranslateLanguage.getAllLanguages());

        spinnerLanguage.setAdapter(arrayAdapter);

    }

    private void setPreviousData(View view) {
        Log.i(TAG, "Set previous data");
        
        try {
            userName.setText(USER_MODEL.getName());
            userEmail.setText(USER_MODEL.getEmail());

            Log.i(TAG, "PATH - " + USER_MODEL.getPathToImage());

            Glide.with(getContext()).load(USER_MODEL.getPathToImage()).into(profileImg);

            if (USER_MODEL.getDateOfBirth() != null) {
                userDOB = USER_MODEL.getDateOfBirth();
                textChooseDOB.setText("Your Date Of Birth is " + USER_MODEL.getDateOfBirth());
            }

            if (USER_MODEL.getLanguageCode() != null) {

                languageCode = USER_MODEL.getLanguageCode();

                Log.i(TAG, "language code - " + languageCode);

                for (int i=0; i < TranslateLanguage.getAllLanguages().size(); i++) {

                    Log.i(TAG, "lang - " + TranslateLanguage.getAllLanguages() + " - " + languageCode);

                    if (languageCode.equals(TranslateLanguage.getAllLanguages().get(i))) {
                        spinnerLanguage.setSelection(i);
                    }

                }
            }

            if (USER_MODEL.getGender() != null) {
                if (USER_MODEL.getGender().equals(((RadioButton) view.findViewById(R.id.radioMale)).getText().toString())) {
                    ((RadioButton) view.findViewById(R.id.radioMale)).setChecked(true);
                } else {
                    ((RadioButton) view.findViewById(R.id.radioFemale)).setChecked(true);
                }
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }


    private void setListeners(View view) {

        textChooseDOB.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(getActivity(), (view1, yearData, monthData, dayOfMonthData) -> {

                Calendar newDate = Calendar.getInstance();
                newDate.set(yearData, monthData, dayOfMonthData);

                userDOB = dayOfMonthData + "." + (monthData+1) + "." + yearData;

                textChooseDOB.setText("Your Date Of Birth is " + userDOB);
            }, year, month, day);

            datePicker.show();

            Log.i(TAG, "DOB - " + userDOB);

        });

        profileImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            pickImage.launch(intent);
        });

        btnUpdate.setOnClickListener(v -> {
            Log.i(TAG, "CHECKING");
            try {
                if (checkData(view)) {

                    Log.i(TAG, "Data Checked");


                    updateUser(
                            userEmail.getText().toString(),
                            userName.getText().toString(),
                            userDOB,
                            radioBtnGender.getText().toString(),
                            languageCode
                    );


                } else {
                    Log.i(TAG, "Incorrect data");
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());

            }
        });

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageCode = TranslateLanguage.getAllLanguages().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

//    private String getLanguageCode(String language) {
//        String langCode;
//
//        switch (language) {
//
//            case "Belarusian":
//                langCode = TranslateLanguage.BELARUSIAN;
//                break;
//
//            case "Danish":
//                langCode = TranslateLanguage.DANISH;
//                break;
//
//            case "German":
//                langCode = TranslateLanguage.GERMAN;
//                break;
//
//            case "Greek":
//                langCode = TranslateLanguage.EL;
//                break;
//
//            case "English":
//                langCode = TranslateLanguage.EN;
//                break;
//
//            case "Spanish":
//                langCode = TranslateLanguage.ES;
//                break;
//
//            default:
//                langCode = TranslateLanguage.RU;
//                break;
//        }
//
//        return langCode;
//    }
//
//    private void setLanguage(int langCode) {
//
//        switch (langCode) {
//            case FirebaseTranslateLanguage.BE:
//                spinnerLanguage.setSelection(0);
//                break;
//
//            case FirebaseTranslateLanguage.DA:
//                spinnerLanguage.setSelection(1);
//                break;
//
//            case FirebaseTranslateLanguage.DE:
//                spinnerLanguage.setSelection(2);
//
//                break;
//
//            case FirebaseTranslateLanguage.EL:
//                spinnerLanguage.setSelection(3);
//                break;
//
//            case FirebaseTranslateLanguage.EN:
//                spinnerLanguage.setSelection(4);
//                break;
//
//            case FirebaseTranslateLanguage.ES:
//                spinnerLanguage.setSelection(5);
//                break;
//
//            default:
//                spinnerLanguage.setSelection(6);
//                break;
//        }
//    }



    private void updateUser(String textEmail, String textName, String textDOB, String textGender, String langCode)  {

        progressBar.show();

        Map<String, Object> userData = new ArrayMap<>();

        userData.put(KEY_EMAIL, textEmail);
        userData.put(KEY_NAME, textName);
        userData.put(KEY_DOB, textDOB);
        userData.put(KEY_GENDER, textGender);
        userData.put(KEY_LANGUAGE_CODE, langCode);

        if(isAddingScore) {
            userData.put(KEY_SCORE, USER_MODEL.getScore() + 50);
        }

        DataBase.updateProfileData(userData, new CompleteListener() {
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

                        Intent intent = new Intent(getActivity(), MainActivity.class);
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

        } else if (TextUtils.isEmpty(userDOB) && !(USER_MODEL.getDateOfBirth() != null)) {
            Toast.makeText(getActivity(), R.string.errorDOB, Toast.LENGTH_SHORT).show();
            textChooseDOB.setError(getResources().getString(R.string.requiredDOB));
            textChooseDOB.requestFocus();

        } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), R.string.errorGender, Toast.LENGTH_SHORT).show();

            // Radio Button does not have this method:
            // radioBtnGender.setError(getResources().getString(R.string.requiredGender));
            // radioBtnGender.requestFocus();

        } else if (languageCode == null) {
            Toast.makeText(getActivity(), "Please choose language", Toast.LENGTH_SHORT).show();

        } else {
            status = true;
        }

        return status;
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
            Log.i(TAG, "PATH" + uri.toString());

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

        })).addOnFailureListener(e -> Toast.makeText(getActivity(), "Something went wrong - " + e.getMessage(), Toast.LENGTH_SHORT).show());
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