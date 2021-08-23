package com.example.chatme.models;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable{
    private String name,email;
    private String image;
    private boolean visibilityStatus;

    public User(){
    }

    public User(String name, String email, String image,boolean visibilityStatus) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.visibilityStatus=visibilityStatus;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isVisibilityStatus() {
        return visibilityStatus;
    }

    public void setVisibilityStatus(boolean visibilityStatus) {
        this.visibilityStatus = visibilityStatus;
    }
}
