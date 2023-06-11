package com.example.englishapp.data.models;

import com.google.gson.annotations.SerializedName;

public class PushNotification {
    @SerializedName("to")
    private String token;

    @SerializedName("data")
    private DataModel data;

    public PushNotification(String token, DataModel data) {
        this.token = token;
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DataModel getData() {
        return data;
    }

    public void setData(DataModel data) {
        this.data = data;
    }
}
