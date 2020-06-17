package com.atakan.unistop_tt.models;

public class ModelUser {

    //use same name as in firebase db
    String name, email, phone, image, usertype, uid, district;
    boolean isBlocked = false;

    public ModelUser() {
    }

    public ModelUser(String name, String email, String phone, String image, String usertype, String uid, String district, boolean isBlocked) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.usertype = usertype;
        this.uid = uid;
        this.district = district;
        this.isBlocked = isBlocked;
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

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}

