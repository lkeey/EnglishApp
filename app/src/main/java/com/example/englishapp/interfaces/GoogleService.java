package com.example.englishapp.interfaces;

import com.example.englishapp.models.SearchRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleService {

    @GET("customsearch/v1")
    Call<SearchRes> find(
            @Query("key") String key,
            @Query("cx") String cx,
            @Query("q") String q,
            @Query("alt") String alt,
            @Query("searchType") String searchType
    );

}
