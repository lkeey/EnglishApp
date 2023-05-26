package com.example.englishapp.testsAndWords;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WikiService {
    @GET("w/rest.php/v1/search/page")
    Call<SearchRes> find(
            @Query("q") String q,
            @Query("limit") int limit
    );

}