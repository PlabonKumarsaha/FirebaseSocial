package com.example.firebasesocial;

public class ModelUser {

    //same name as firebase instances are given

    public ModelUser(String name, String email, String phone, String image, String cover, String uid) {
        Name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.uid = uid;
    }

    String Name,email,phone,image,cover,uid;


    public ModelUser() {

    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



}
