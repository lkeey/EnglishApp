package com.example.englishapp.messaging;

import static com.example.englishapp.database.Constants.REMOTE_MSG_DATA;
import static com.example.englishapp.database.Constants.REMOTE_MSG_TITLE;
import static com.example.englishapp.database.Constants.REMOTE_MSG_USER_SENDER;

import com.google.gson.annotations.SerializedName;

public class DataModel {

    @SerializedName(REMOTE_MSG_TITLE)
    private String title;

    @SerializedName(REMOTE_MSG_DATA)
    private String body;

    @SerializedName(REMOTE_MSG_USER_SENDER)
    private String senderUID;

    public DataModel(String title, String body, String senderUID) {
        this.title = title;
        this.body = body;
        this.senderUID = senderUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }
}
