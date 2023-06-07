package com.example.englishapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleResults {
    @SerializedName("items")
    public List<GoogleItem> items;

    public GoogleResults(List<GoogleItem> items) {
        this.items = items;
    }

    public List<GoogleItem> getItems() {
        return items;
    }

    public void setItems(List<GoogleItem> items) {
        this.items = items;
    }
}
