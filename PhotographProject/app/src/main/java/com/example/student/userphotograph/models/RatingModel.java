package com.example.student.userphotograph.models;

import org.parceler.Parcel;

@Parcel

public class RatingModel {
    private String name;
    private int rating;
    private String email;
    private String address;
    private String camera_info;
    private String imageUri;
    private String avatarUri;
    private String title;
    private String uid;

    public RatingModel() {

    }

    public RatingModel(String name, int rating, String email, String address, String camera_info,
                       String imageUri, String avatarUri, String title, String uid) {
        this.name = name;
        this.rating = rating;
        this.email = email;
        this.address = address;
        this.camera_info = camera_info;
        this.imageUri = imageUri;
        this.avatarUri = avatarUri;
        this.title = title;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCamera_info() {
        return camera_info;
    }

    public void setCamera_info(String camera_info) {
        this.camera_info = camera_info;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
