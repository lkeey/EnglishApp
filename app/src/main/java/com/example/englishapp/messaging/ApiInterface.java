package com.example.englishapp.messaging;

import static com.example.englishapp.messaging.Constants.CONTENT_TYPE;
import static com.example.englishapp.messaging.Constants.SERVER_KEY;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ApiInterface {

    @Headers({SERVER_KEY, CONTENT_TYPE})
    @POST("fcm/send")
    static Call<PushNotification> sendNotification(@Body PushNotification notification) {
        return (Call<PushNotification>) notification;
    }

}
