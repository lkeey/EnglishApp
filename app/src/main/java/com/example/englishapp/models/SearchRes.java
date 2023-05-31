package com.example.englishapp.models;

public class SearchRes {
    public Page[] pages;

    public SearchRes(Page[] pages) {
        this.pages = pages;
    }

    public Page[] getPages() {
        return pages;
    }

    public void setPages(Page[] pages) {
        this.pages = pages;
    }
}
