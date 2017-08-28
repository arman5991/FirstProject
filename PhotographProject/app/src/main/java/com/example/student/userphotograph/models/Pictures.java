package com.example.student.userphotograph.models;

public class Pictures {

    private String title;
    private String imageUri;
    private String imageName;

    public Pictures() {
    }

    public Pictures(String title, String imageUri, String imageName) {
        this.title = title;
        this.imageUri = imageUri;
        this.imageName = imageName;
}

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {return imageName;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUri() {
        return imageUri;
    }

}
