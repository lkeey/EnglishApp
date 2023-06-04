package com.example.englishapp.messaging;

import com.example.englishapp.database.Constants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationService {

    @Headers({"Authorization: key=" + Constants.SERVER_KEY, "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body PushNotification notification);
}
