package com.example.englishapp.MVP;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String uid;
    private String name;
    private String email;
    private String gender;
    private String mobile;
    private String pathToImage;
    private String dateOfBirth;
    private int score;
    private int bookmarksCount;

    public UserModel(String uid, String name, String email, String gender, String mobile, String pathToImage, String dateOfBirth, int score, int bookmarksCount) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.mobile = mobile;
        this.pathToImage = pathToImage;
        this.dateOfBirth = dateOfBirth;
        this.score = score;
        this.bookmarksCount = bookmarksCount;
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
}
