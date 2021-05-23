package com.example.sample.providerapp.Model;

/**
 * Created by Hassan Javaid on 9/5/2018.
 */

public class Billing {
    private String price;
    private String image;
    private String uid;
    private String name;

    public Billing(String price, String image, String uid,String name) {
        this.price = price;
        this.image = image;
        this.uid = uid;
        this.name=name;
    }

    public Billing() {

    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
