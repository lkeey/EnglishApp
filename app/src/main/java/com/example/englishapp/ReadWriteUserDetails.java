package com.example.englishapp;

public class ReadWriteUserDetails {
    public String userDOB, userGender, userMobile;

    public ReadWriteUserDetails() {

    };

    public ReadWriteUserDetails(String textDOB, String textGender, String textMobile) {
        this.userDOB = textDOB;
        this.userGender = textGender;
        this.userMobile = textMobile;
    }
}
