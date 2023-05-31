package com.example.englishapp.interfaces;

import com.example.englishapp.models.GoogleResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleService {

//    https://www.googleapis.com/customsearch/v1?key=AIzaSyDdBPCVzYyCmtFtZSSihqOSUsPZglM5x3E&cx=42a504d9a5afa4755&q=car&alt=json&searchType=image
    @GET("customsearch/v1")
    Call<GoogleResults> find(
            @Query("key") String key,
            @Query("cx") String cx,
            @Query("q") String q,
            @Query("alt") String alt,
            @Query("searchType") String searchType
    );

}
