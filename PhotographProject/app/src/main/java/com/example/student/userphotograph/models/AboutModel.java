package com.example.student.userphotograph.models;

public class AboutModel {
    private Integer imgMember;
    private String tvMemberName;

    public AboutModel() {
    }

    public AboutModel(Integer imgMember, String tvMemberName) {
        this.imgMember = imgMember;
        this.tvMemberName = tvMemberName;
    }

    public Integer getImgMember() {
        return imgMember;
    }

    public void setImgMember(Integer imgMember) {
        this.imgMember = imgMember;
    }

    public String getTvMemberName() {
        return tvMemberName;
    }

    public void setTvMemberName(String tvMemberName) {
        this.tvMemberName = tvMemberName;
    }
}