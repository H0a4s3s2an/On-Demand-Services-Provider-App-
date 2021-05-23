package com.example.sample.providerapp.Model;

/**
 * Created by Hassan Javaid on 9/2/2018.
 */

public class User {
    public String uid;
    public String fname;
    public String lname;
    public String email;
    public String password;
    public String mobile;
    public String picture;
    public double wallet_balance;
    public String rating;
    public String timestamp;
    public double latitude;
    public double longitude;

    public User()
    {
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User(String uid, String fname, String lname, String email, String password, String picture, double wallet_balance, String rating, int otp, String timestamp,double latitude,double longitude) {
        this.uid = uid;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.wallet_balance = wallet_balance;
        this.rating = rating;
        this.timestamp = timestamp;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double getWallet_balance() {
        return wallet_balance;
    }

    public void setWallet_balance(double wallet_balance) {
        this.wallet_balance = wallet_balance;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
