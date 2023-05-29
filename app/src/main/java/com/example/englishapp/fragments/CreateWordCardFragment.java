package com.example.englishapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.englishapp.interfaces.CompleteListener;
import com.example.englishapp.database.DataBase;
import com.example.englishapp.activities.MainActivity;
import com.example.englishapp.models.SearchRes;
import com.example.englishapp.models.Word;
import com.example.englishapp.R;
import com.example.englishapp.interfaces.GoogleService;
import com.example.englishapp.interfaces.WikiService;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateWordCardFragment extends Fragment {

    private static final String TAG = "CreateWordCardFragment";
    private Retrofit retrofit;
    private WikiService service;
    private GoogleService googleService;
    private LinearLayout layoutList;
    private Button btnAdd, btnSubmit;
    private Spinner spinnerLevels;
    private EditText cardName, cardDescription;
    private Dialog progressBar;
    private TextView dialogText;
    private final ArrayList<Word> listOfWords = new ArrayList<>();
    private List<String> stringListLevels = new ArrayList<>();
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

                DataBase.createCardData(listOfWords, cardName.getText().toString(), cardDescription.getText().toString(), spinnerLevels.getSelectedItem().toString(), new CompleteListener() {
                    @Override
                    public void OnSuccess() {
                        Log.i(TAG, "Card successfully created");

                        Toast.makeText(getActivity(), "Card Successfully Created", Toast.LENGTH_SHORT).show();

                        ((MainActivity) getActivity()).setFragment(new CategoryFragment());

                        progressBar.dismiss();
                    }

                    @Override
                    public void OnFailure() {
                        Log.i(TAG, "Can not create card");

                        Toast.makeText(getActivity(), "Error occurred... Try later", Toast.LENGTH_SHORT).show();

                        ((MainActivity) getActivity()).setFragment(new CategoryFragment());

                        progressBar.dismiss();
                    }
                });
            }
        });
    }

    private boolean checkData() {

        listOfWords.clear();

        if (cardName.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Name must be not empty", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (cardDescription.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Description must be not empty", Toast.LENGTH_SHORT).show();

            return false;
        }

        for (int i=0; i < layoutList.getChildCount(); i++) {
            View viewChild = layoutList.getChildAt(i);

            EditText textWord = viewChild.findViewById(R.id.editWord);
            ImageView imgWord = viewChild.findViewById(R.id.imageWord);
            EditText descriptionWord = viewChild.findViewById(R.id.wordDescription);

            Word wordModel = new Word();

            if (textWord.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Name of word must be not empty", Toast.LENGTH_SHORT).show();

                return false;
            } else {
                wordModel.setTextEn(textWord.getText().toString());
            }

            if (descriptionWord.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Description of word must be not empty", Toast.LENGTH_SHORT).show();

                return false;
            } else {
                wordModel.setDescription(descriptionWord.getText().toString());
            }

            try {

                Bitmap bmp = ((BitmapDrawable) imgWord.getDrawable()).getBitmap();

                Log.i(TAG, "bmp - " + bmp.toString());

                if (bmp.toString().isEmpty()) {

                    Toast.makeText(getActivity(), "Find or Load Image", Toast.LENGTH_SHORT).show();

                    return false;

                } else {

                    Log.i(TAG, "set img - " + bmp);
                    wordModel.setImage(bmp);
                }

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());

                Toast.makeText(getActivity(), "Can not read image", Toast.LENGTH_SHORT).show();

                return false;
            }

            listOfWords.add(wordModel);

        }

        return true;
    }

    private void addView() {
        View view = getLayoutInflater().inflate(R.layout.word_add, null, false);

        ImageView imageView = view.findViewById(R.id.imageRemove);
        ImageView imgWord = view.findViewById(R.id.imageWord);
        EditText wordDescription = view.findViewById(R.id.wordDescription);
        EditText wordText = view.findViewById(R.id.editWord);
        Button btnSearch = view.findViewById(R.id.btnSearch);
        ProgressBar progress = view.findViewById(R.id.progressBar);

        imageView.setOnClickListener(v -> removeView(view));

        btnSearch.setOnClickListener(v -> {

            if (wordText.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Write text", Toast.LENGTH_SHORT).show();
            } else {
                progress.setVisibility(View.VISIBLE);
                btnSearch.setEnabled(false);

                // search image and description in wikipedia
                Call<SearchRes> call = service.find(wordText.getText().toString(), 1);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<SearchRes> call, @NonNull Response<SearchRes> response) {
                        SearchRes res = response.body();
                        if (response.isSuccessful() && res.pages != null && res.pages.length > 0) {

                            // set image and description to layout

                            wordDescription.setText(Html.fromHtml(res.pages[0].excerpt, Html.FROM_HTML_MODE_COMPACT));

                            if (res.pages[0].thumbnail != null) {

                                getActivity().runOnUiThread(() -> {
                                    try {
                                        Log.i(TAG, "find img - " + "https:" + res.pages[0].thumbnail.url);

                                        URL url = new URL("https:" + res.pages[0].thumbnail.url);

                                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                                        Log.i(TAG, "image - " + image.getWidth() + " - " + image.getHeight());

                                        imgWord.setImageBitmap(image);

                                        Log.i(TAG, "bitmap successfully set");

                                    } catch (MalformedURLException e) {
                                        Log.i(TAG, "er - " + e.getMessage());

                                    } catch (Exception e) {
                                        Log.i(TAG, "e - " + e.getClass());
                                    }                                    });

                                Log.i(TAG, "ok");

                            } else {
                                Log.i(TAG, "not found image");
                            }

                            // user can again search another word
//                              wordText.setFocusableInTouchMode(true);

                            progress.setVisibility(View.GONE);
                            btnSearch.setEnabled(true);

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SearchRes> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_SHORT).show();

                        progress.setVisibility(View.GONE);
                        btnSearch.setEnabled(true);

                    }
                });


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

        ((MainActivity) getActivity()).setTitle(R.string.nameCreateCardWord);

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

        dialogText = progressBar.findViewById(R.id.dialogText);
        dialogText.setText(R.string.progressBarSaving);

        stringListLevels.add("A1");
        stringListLevels.add("A2");
        stringListLevels.add("B1");
        stringListLevels.add("B2");
        stringListLevels.add("C1");
        stringListLevels.add("C2");

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, stringListLevels);
        spinnerLevels.setAdapter(adapter);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WikiService.class);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        googleService = retrofit.create(GoogleService.class);

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

                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imgUri);
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