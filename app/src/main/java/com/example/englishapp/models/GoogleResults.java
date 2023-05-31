package com.example.englishapp.models;

public class GoogleResults {

    public GooglePage[] pages;

    public GoogleResults(GooglePage[] pages) {
        this.pages = pages;
    }

    public GooglePage[] getPages() {
        return pages;
    }


}
