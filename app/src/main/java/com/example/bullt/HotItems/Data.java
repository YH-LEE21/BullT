package com.example.bullt.HotItems;

public class Data {

    //상표명,상품종류,가격,이미지,주소
    private String title;
    private String content;
    private String price;
    private int resId;
    private String path;
    private boolean like;

    public Data (String title,String content,String price,int resId,String path,boolean like){
        this.title = title;
        this.content = content;
        this.price = price;
        this.resId = resId;
        this.path = path;
        this.like = like;
    }
    public Data(){}

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

    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public boolean getLike(){return like;}

    public void setLike(){this.like = like;}
}
