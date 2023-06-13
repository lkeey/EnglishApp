package com.example.englishapp.presentation.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.englishapp.data.database.Constants.KEY_ADD_SCORE;
import static com.example.englishapp.data.database.Constants.KEY_IS_CHANGING_WALLPAPER;
import static com.example.englishapp.data.database.Constants.MY_SHARED_PREFERENCES;
import static com.example.englishapp.data.database.DataBasePersonalData.USER_MODEL;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.repositories.UpdateProfileRepository;
import com.example.englishapp.presentation.activities.MainActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class ProfileInfoFragment extends Fragment {

    private static final String TAG = "UpdateProfileInfo";
    private String userDOB;
    private EditText userName, userEmail;
    private Dialog progressBar;
    private Button btnUpdate;
    private ImageView profileImg;
    private RadioGroup radioGroupGender;
    private RadioButton radioBtnGender;
    private TextView textChooseDOB;
    private ActivityResultLauncher<Intent> pickImage;
    private DatePickerDialog datePicker;
    private Spinner spinnerLanguage;
    private Uri imgUri;
    private String languageCode;
    private boolean isAddingScore;
    private SwitchMaterial switcherWallpaper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        init(view);

        setPicker();

        setPreviousData(view);

        setListeners(view);

        receiveData();

        return view;
    }

    private void init(View view) {
        requireActivity().setTitle(R.string.nameProfileInfo);

        userName = view.findViewById(R.id.editTextName);
        userEmail = view.findViewById(R.id.editTextEmail);
        textChooseDOB = view.findViewById(R.id.textChooseDOB);
        btnUpdate = view.findViewById(R.id.btnSignUp);
        radioGroupGender = view.findViewById(R.id.groupGender);
        profileImg = view.findViewById(R.id.imageUser);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        switcherWallpaper = view.findViewById(R.id.switcherWallpaper);
        userEmail.setEnabled(false);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarUpdating);

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, TranslateLanguage.getAllLanguages());

        spinnerLanguage.setAdapter(arrayAdapter);
    }


    private void receiveData() {

        Bundle bundle = getArguments();

        if (bundle != null) {
            isAddingScore = bundle.getBoolean(KEY_ADD_SCORE, false);
        }
    }

    private void setPicker() {
        pickImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            imgUri = result.getData().getData();

                            try {
                                Log.i(TAG, "set bitmap");

                                InputStream inputStream = requireActivity().getContentResolver().openInputStream(imgUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                profileImg.setImageBitmap(bitmap);

                                profileImg.setBackground(null);

                                Log.i(TAG, "set bitmap 2");

                            } catch (FileNotFoundException e) {
                                Log.i(TAG, e.getMessage());
                            }
                        }
                    }
                }
        );
    }


    private void setPreviousData(View view) {
        Log.i(TAG, "Set previous data");
        
        try {
            userName.setText(USER_MODEL.getName());
            userEmail.setText(USER_MODEL.getEmail());

            Log.i(TAG, "PATH - " + USER_MODEL.getPathToImage());

            Glide.with(requireActivity()).load(USER_MODEL.getPathToImage()).into(profileImg);
            profileImg.setBackground(null);

            if (USER_MODEL.getDateOfBirth() != null) {
                userDOB = USER_MODEL.getDateOfBirth();
                textChooseDOB.setText(getString(R.string.dateOfBirth) + " " + USER_MODEL.getDateOfBirth());
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

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
            boolean isShowing = sharedPreferences.getBoolean(KEY_IS_CHANGING_WALLPAPER, false);

            switcherWallpaper.setChecked(isShowing);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }


    private void setListeners(View view) {
        textChooseDOB.setOnClickListener(v -> chooseDOB());

        profileImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            pickImage.launch(intent);
        });

        btnUpdate.setOnClickListener(v -> updateData(view));

        userEmail.setOnClickListener(v -> Toast.makeText(getActivity(), getString(R.string.you_can_not_change_your_e_mail), Toast.LENGTH_SHORT).show());

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageCode = TranslateLanguage.getAllLanguages().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        switcherWallpaper.setOnCheckedChangeListener((buttonView, isChecked) -> updateWallpaperChanging(isChecked));
    }

    private void chooseDOB() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePicker = new DatePickerDialog(getActivity(), (view1, yearData, monthData, dayOfMonthData) -> {

            Calendar newDate = Calendar.getInstance();
            newDate.set(yearData, monthData, dayOfMonthData);

            userDOB = dayOfMonthData + "." + (monthData+1) + "." + yearData;

            textChooseDOB.setText(getString(R.string.dateOfBirth)  + " " +  userDOB);
        }, year, month, day);

        datePicker.show();

        Log.i(TAG, "DOB - " + userDOB);
    }

    private void updateData(View view) {
        if (checkData(view)) {

            Log.i(TAG, "Data Checked");

            progressBar.show();

            new UpdateProfileRepository().updateUser(
                    userEmail.getText().toString(), userName.getText().toString(),
                    userDOB, radioBtnGender.getText().toString(),
                    languageCode, isAddingScore,
                    imgUri, getContext(), new CompleteListener() {
                        @Override
                        public void OnSuccess() {
                            Log.i(TAG, "Successfully set data");
                            Toast.makeText(getActivity(), getString(R.string.personal_data_successfully_updated), Toast.LENGTH_SHORT).show();
                            progressBar.dismiss();

                            ((MainActivity) requireActivity()).setFragment(new ProfileFragment(), true);
                            ((MainActivity) requireActivity()).setCheckedNavigationIcon(3);
                        }

                        @Override
                        public void OnFailure() {
                            Log.i(TAG, "Fail to set data");
                            Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                            progressBar.dismiss();
                        }
                    }
            );
        }
    }

    private void updateWallpaperChanging(Boolean isChecked) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putBoolean(KEY_IS_CHANGING_WALLPAPER, isChecked);

        myEdit.apply();
    }

    private boolean checkData(View view) {
        boolean status = false;

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        radioBtnGender = view.findViewById(selectedGenderId);

        String textName = userName.getText().toString();

        if (TextUtils.isEmpty(textName)) {
            Toast.makeText(getActivity(), R.string.errorName, Toast.LENGTH_SHORT).show();
            userName.setError(getResources().getString(R.string.requiredName));
            userName.requestFocus();

        } else if (TextUtils.isEmpty(userDOB) && USER_MODEL.getDateOfBirth() == null) {
            Toast.makeText(getActivity(), R.string.errorDOB, Toast.LENGTH_SHORT).show();
            textChooseDOB.setError(getResources().getString(R.string.requiredDOB));
            textChooseDOB.requestFocus();

        } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), R.string.errorGender, Toast.LENGTH_SHORT).show();

        } else if (languageCode == null) {
            Toast.makeText(getActivity(), getString(R.string.please_choose_language), Toast.LENGTH_SHORT).show();

        } else {
            status = true;
        }

        return status;
    }
}
