package com.example.englishapp.domain.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMApi {
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    private static Retrofit retrofit;

    private static Retrofit create() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public static Retrofit getInstance() {
        if (retrofit == null) retrofit = create();
        return retrofit;
    }
}
