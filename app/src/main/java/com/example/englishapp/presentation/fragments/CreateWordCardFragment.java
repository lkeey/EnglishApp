package com.example.englishapp.presentation.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.presentation.activities.MainActivity;
import com.example.englishapp.data.database.DataBaseCards;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.repositories.CreateCardRepository;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateWordCardFragment extends Fragment {

    private static final String TAG = "CreateWordCardFragment";
    private CreateCardRepository createCardRepository;
    private LinearLayout layoutList;
    private Button btnAdd, btnSubmit;
    private Spinner spinnerLevels;
    private EditText cardName, cardDescription;
    private Dialog progressBar;
    private final List<String> stringListLevels = new ArrayList<>();
    private ActivityResultLauncher<Intent> pickImage;
    private ImageView chosenImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_word_card, container, false);
        try {
            init(view);

            setListeners();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
        return view;
    }

    private void setListeners() {

        btnAdd.setOnClickListener(view -> addView());

        btnSubmit.setOnClickListener(v -> {
            if (checkData()) {
                progressBar.show();

                Log.i(TAG, "pos - " + spinnerLevels.getSelectedItem().toString());

                DataBaseCards dataBaseCards = new DataBaseCards();

                dataBaseCards.createCardData(CreateCardRepository.listOfWords, cardName.getText().toString(), cardDescription.getText().toString(), spinnerLevels.getSelectedItem().toString(), new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "Card successfully created");

                        Toast.makeText(getActivity(), getString(R.string.card_successfully_created), Toast.LENGTH_SHORT).show();

                        ((MainActivity) requireActivity()).setFragment(new CategoryFragment());

                        progressBar.dismiss();
                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Can not create card");

                        Toast.makeText(getActivity(), getString(R.string.error_occurred_try_later), Toast.LENGTH_SHORT).show();

                        ((MainActivity) requireActivity()).setFragment(new CategoryFragment());

                        progressBar.dismiss();
                    }
                });
            }
        });
    }

    private boolean checkData() {

        int status = createCardRepository.checkData(
                cardName.getText().toString(),
                cardDescription.getText().toString(),
                layoutList
        );

        if (status == CreateCardRepository.CODE_NAME) {
            Toast.makeText(getActivity(), getString(R.string.name_must_be_not_empty), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CreateCardRepository.CODE_DESCRIPTION) {
            Toast.makeText(getActivity(), getString(R.string.description_must_be_not_empty), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CreateCardRepository.CODE_NAME_WORD) {
            Toast.makeText(getActivity(), getString(R.string.name_of_word_must_be_not_empty), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CreateCardRepository.CODE_DESCRIPTION_WORD) {
            Toast.makeText(getActivity(), getString(R.string.description_of_word_must_be_not_empty), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CreateCardRepository.CODE_IMAGE_WORD) {
            Toast.makeText(getActivity(), getString(R.string.find_or_load_image), Toast.LENGTH_SHORT).show();

            return false;
        } else if (status == CreateCardRepository.CODE_READ_IMAGE_WORD) {
            Toast.makeText(getActivity(), getString(R.string.can_not_read_image), Toast.LENGTH_SHORT).show();

            return false;
        } else {
            return true;
        }
    }

    private void addView() {
        View view = getLayoutInflater().inflate(R.layout.word_add, null, false);

        ImageView imageView = view.findViewById(R.id.imageRemove);
        ImageView imgWord = view.findViewById(R.id.imageWord);
        EditText wordDescription = view.findViewById(R.id.wordDescription);
        EditText wordText = view.findViewById(R.id.editWord);
        Button btnSearchWiki = view.findViewById(R.id.btnSearchWiki);
        Button btnSearchGoogle = view.findViewById(R.id.btnSearchGoogle);

        ProgressBar progress = view.findViewById(R.id.progressBar);

        imageView.setOnClickListener(v -> removeView(view));

        btnSearchWiki.setOnClickListener(v -> {

            if (wordText.getText().toString().isEmpty()) {

                Toast.makeText(getActivity(), getString(R.string.write_text), Toast.LENGTH_SHORT).show();

            } else {

                progress.setVisibility(View.VISIBLE);
                btnSearchWiki.setEnabled(false);

                // search image and description in wikipedia
                createCardRepository.sendWikipediaRequest(wordText, wordDescription, imgWord, btnSearchWiki, progress);

            }
        });

        btnSearchGoogle.setOnClickListener(v -> {
            if (wordText.getText().toString().isEmpty()) {

                Toast.makeText(getActivity(), getString(R.string.write_text), Toast.LENGTH_SHORT).show();

            } else {

                progress.setVisibility(View.VISIBLE);
                btnSearchGoogle.setEnabled(false);

                // search image in google
                createCardRepository.sendGoogleRequest(wordText, imgWord, btnSearchGoogle, progress);
            }
        });

        imgWord.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            pickImage.launch(intent);

            chosenImg = imgWord;
        });

        layoutList.addView(view);
    }

    private void removeView(View view) {
        layoutList.removeView(view);
    }

    private void init(View view) {

        ((MainActivity) requireActivity()).setTitle(R.string.nameCreateCardWord);

        layoutList = view.findViewById(R.id.layoutListWords);
        btnAdd = view.findViewById(R.id.btnAddWord);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        cardName = view.findViewById(R.id.cardName);
        cardDescription = view.findViewById(R.id.cardDescription);
        spinnerLevels = view.findViewById(R.id.spinnerLevels);

        progressBar = new Dialog(getActivity());
        progressBar.setContentView(R.layout.dialog_layout);
        progressBar.setCancelable(false);
        progressBar.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarSaving);

        stringListLevels.add("A1");
        stringListLevels.add("A2");
        stringListLevels.add("B1");
        stringListLevels.add("B2");
        stringListLevels.add("C1");
        stringListLevels.add("C2");

        ArrayAdapter adapter = new ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, stringListLevels);
        spinnerLevels.setAdapter(adapter);

        createCardRepository = new CreateCardRepository();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            Uri imgUri = result.getData().getData();

                        try {
                            Log.i(TAG, "set bitmap");

                            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imgUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            chosenImg.setImageBitmap(bitmap);

                        } catch (FileNotFoundException e) {
                            Log.i(TAG, e.getMessage());
                        }
                    }
                }
            }
        );

    }
}
