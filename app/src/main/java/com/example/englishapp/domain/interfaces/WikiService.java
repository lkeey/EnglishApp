package com.example.englishapp.domain.interfaces;

import com.example.englishapp.data.models.SearchRes;

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
