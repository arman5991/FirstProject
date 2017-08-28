package com.example.student.userproject.model;


import java.util.Date;

public class PostModel {

    private long date;
    private String imageUrl;
    private long likes;
    private String title;
    private String userName;
    private String uid;
    private String userId;


    public PostModel() {

    }

    public PostModel(long date, String imageUrl, long likes, String title, String userName, String uid, String userId) {
        this.date = date;
        this.imageUrl = imageUrl;
        this.likes = likes;
        this.title = title;
        this.userName = userName;
        this.uid = uid;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}


