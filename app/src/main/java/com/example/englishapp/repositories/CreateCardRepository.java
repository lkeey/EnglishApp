package com.example.englishapp.repositories;

import static com.example.englishapp.database.Constants.GOOGLE_API_KEY;
import static com.example.englishapp.database.Constants.GOOGLE_CX;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.englishapp.R;
import com.example.englishapp.interfaces.GoogleService;
import com.example.englishapp.interfaces.WikiService;
import com.example.englishapp.models.GoogleResults;
import com.example.englishapp.models.SearchRes;
import com.example.englishapp.models.WordModel;
import com.example.englishapp.services.GoogleApi;
import com.example.englishapp.services.WikiApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCardRepository {

    private static final String TAG = "RepositoryCreateCard";
    private static final int IMG_WIDTH = 300;
    private static final int IMG_HEIGHT = 300;
    public static final ArrayList<WordModel> listOfWords = new ArrayList<>();
    public static int CODE_SUCCESS = 0;
    public static int CODE_NAME = 1;
    public static int CODE_DESCRIPTION = 2;
    public static int CODE_NAME_WORD = 3;
    public static int CODE_DESCRIPTION_WORD = 4;
    public static int CODE_IMAGE_WORD = 5;
    public static int CODE_READ_IMAGE_WORD = 6;

    public void sendGoogleRequest(EditText wordText, ImageView imgWord, Button btnSearch, ProgressBar progress) {

        GoogleService serviceGoogle = GoogleApi.getInstance().create(GoogleService.class);

        Call<GoogleResults> call = serviceGoogle.find(
                GOOGLE_API_KEY,
                GOOGLE_CX,
                wordText.getText().toString(),
                "json",
                "image"
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GoogleResults> call, @NonNull Response<GoogleResults> response) {
                GoogleResults res = response.body();

                if (response.isSuccessful()) {
                    Log.i(TAG, "url - " + Objects.requireNonNull(res).getItems().get(0).getImage().getUrl());
                    try {
                        URL url = new URL(Objects.requireNonNull(res).getItems().get(0).getImage().getUrl());

                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        Log.i(TAG, "image - " + image.getWidth() + " - " + image.getHeight());

                        // rise the height and width
                        Bitmap scaledBmp = Bitmap.createScaledBitmap(image, IMG_WIDTH, IMG_HEIGHT, true);

                        Log.i(TAG, "scaledBmp - " + scaledBmp.getWidth() + " - " + scaledBmp.getHeight());

                        imgWord.setImageBitmap(scaledBmp);

                        Log.i(TAG, "bitmap successfully set");

                        imgWord.setBackground(null);

                        progress.setVisibility(View.GONE);
                        btnSearch.setEnabled(true);

                    } catch (IOException e) {
                        Log.i(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleResults> call, @NonNull Throwable t) {
                Log.i(TAG, t.getMessage());

                progress.setVisibility(View.GONE);
                btnSearch.setEnabled(true);
            }
        });

    }

    public void sendWikipediaRequest(EditText wordText, EditText wordDescription, ImageView imgWord, Button btnSearch, ProgressBar progress){

        WikiService serviceWiki = WikiApi.getInstance().create(WikiService.class);

        Call<SearchRes> call = serviceWiki.find(wordText.getText().toString(), 1);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SearchRes> call, @NonNull Response<SearchRes> response) {
                SearchRes res = response.body();
                if (response.isSuccessful() && res != null) {
                    if (res.getPages() != null && res.getPages().length > 0) {

                        // set image and description to layout

                        wordDescription.setText(Html.fromHtml(res.getPages()[0].getExcerpt(), Html.FROM_HTML_MODE_COMPACT));

                        if (res.getPages()[0].getThumbnail() != null) {

                            try {
                                Log.i(TAG, "find img - " + "https:" + res.getPages()[0].getThumbnail().getUrl());

                                URL url = new URL("https:" + res.getPages()[0].getThumbnail().getUrl());

                                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                                Log.i(TAG, "image - " + image.getWidth() + " - " + image.getHeight());

                                // rise the height and width
                                Bitmap scaledBmp = Bitmap.createScaledBitmap(image, 200, 200, true);

                                Log.i(TAG, "scaledBmp - " + scaledBmp.getWidth() + " - " + scaledBmp.getHeight());

                                imgWord.setImageBitmap(scaledBmp);

                                Log.i(TAG, "bitmap successfully set");

                                imgWord.setBackground(null);

                            } catch (MalformedURLException e) {
                                Log.i(TAG, "er - " + e.getMessage());

                            } catch (Exception e) {
                                Log.i(TAG, "e - " + e.getClass());
                            }


                            Log.i(TAG, "ok");

                        } else {
                            Log.i(TAG, "not found image");
                        }

                        progress.setVisibility(View.GONE);
                        btnSearch.setEnabled(true);

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchRes> call, @NonNull Throwable t) {

                progress.setVisibility(View.GONE);
                btnSearch.setEnabled(true);

            }
        });
    }

    public int checkData(String card, String description, LinearLayout layoutList) {

        listOfWords.clear();

        if (card.isEmpty()) {
            return CODE_NAME;
        }

        if (description.isEmpty()) {
            return CODE_DESCRIPTION;
        }

        for (int i=0; i < layoutList.getChildCount(); i++) {
            View viewChild = layoutList.getChildAt(i);

            EditText textWord = viewChild.findViewById(R.id.editWord);
            ImageView imgWord = viewChild.findViewById(R.id.imageWord);
            EditText descriptionWord = viewChild.findViewById(R.id.wordDescription);

            WordModel wordModel = new WordModel();

            if (textWord.getText().toString().isEmpty()) {
                return CODE_NAME_WORD;

            } else {
                wordModel.setTextEn(textWord.getText().toString());
            }

            if (descriptionWord.getText().toString().isEmpty()) {

                return CODE_DESCRIPTION_WORD;

            } else {
                wordModel.setDescription(descriptionWord.getText().toString());
            }

            try {

                Bitmap bmp = ((BitmapDrawable) imgWord.getDrawable()).getBitmap();

                Log.i(TAG, "bmp - " + bmp.toString());

                if (bmp.toString().isEmpty()) {

                    return CODE_IMAGE_WORD;

                } else {

                    Log.i(TAG, "set img - " + bmp);
                    wordModel.setImage(bitMapToString(bmp));
                }

                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());

                    return CODE_READ_IMAGE_WORD;
                }

                listOfWords.add(wordModel);

            }

        return CODE_SUCCESS;
    }

    public String bitMapToString(Bitmap bitmap){

        ByteArrayOutputStream bmp = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100, bmp);

        byte[] b = bmp.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }


}
