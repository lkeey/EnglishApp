package com.example.englishapp.chat;

import static com.example.englishapp.messaging.Constants.SERVER_KEY;

import com.example.englishapp.messaging.PushNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Authorization: key=" + SERVER_KEY
    })
    @POST("fcm/send")
    Call<PushNotification> sendNotification(
            @Body PushNotification notification
    );
}
