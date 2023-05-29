package com.example.englishapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage implements Comparable<ChatMessage> {
    public String senderId;
    public String receiverId;
    public String message;
    public Date dateTime;

    public ChatMessage(String senderId, String receiverId, String message, Date dateTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateTime() {
        return dateTime;
    }
    public String getBeautyDateTime() {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public int compareTo(ChatMessage o) {
        return this.dateTime.compareTo(o.dateTime);
    }

}
