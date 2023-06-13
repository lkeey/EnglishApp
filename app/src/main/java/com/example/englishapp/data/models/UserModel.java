package com.example.englishapp.data.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String uid;
    private String name;
    private String email;
    private String gender;
    private String mobile;
    private String pathToImage;
    private String dateOfBirth;
    private String fcmToken;
    private String languageCode;
    private int score;
    private int bookmarksCount;
    private int place;
    private double latitude;
    private double longitude;

    public UserModel() {}

    public UserModel(String uid, String name, String email, String gender, String mobile, String pathToImage, String dateOfBirth, String fcmToken, int score, int bookmarksCount, int place, String languageCode, double latitude, double longitude) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.mobile = mobile;
        this.pathToImage = pathToImage;
        this.dateOfBirth = dateOfBirth;
        this.fcmToken = fcmToken;
        this.score = score;
        this.bookmarksCount = bookmarksCount;
        this.place = place;
        this.languageCode = languageCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public int getScore() {
        return score;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBookmarksCount() {
        return bookmarksCount;
    }

    public void setBookmarksCount(int bookmarksCount) {
        this.bookmarksCount = bookmarksCount;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
