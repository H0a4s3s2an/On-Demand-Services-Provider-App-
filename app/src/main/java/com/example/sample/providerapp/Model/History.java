package com.example.sample.providerapp.Model;

/**
 * Created by Hassan Javaid on 9/3/2018.
 */

public class History {

    public double price;
    public String category;
    public String image;
    public String time;
    public String historyId;
    public String status;

    public History() {
    }

    public History(double price, String category, String image, String time,String historyId,String status) {
        this.price = price;
        this.category = category;
        this.image = image;
        this.time = time;
        this.historyId=historyId;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
