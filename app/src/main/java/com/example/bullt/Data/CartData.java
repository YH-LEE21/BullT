package com.example.bullt.Data;

public class CartData {
    String imagePath;
    String title;
    String content;
    int price;
    String size="S";
    int count=1;
    int totalPrice;
    String id;

    public CartData(){}



    public CartData(String id, String imagePath, String title, String content, int price , String size, int count, int totalprice){
        this.id = id;
        this.imagePath = imagePath;
        this.title = title;
        this.content = content;
        this.price = price;
        this.size = size;
        this.count = count;
        this.totalPrice = totalPrice;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getTotalprice() {
        return totalPrice;
    }

    public void setTotalprice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
