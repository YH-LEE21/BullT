package com.example.bullt.Data;

public class ItemData {

    //상표명,상품종류,가격,이미지,주소
    private String title;
    private String content;
    private String price;
    private String resId;
    private String path;
    private String search;



    private String imageId;
    private int count;

    public ItemData(String title, String content, String price, String resId, String path, String imageID, int count,String search){

        this.title = title;
        this.content = content;
        this.price = price;
        this.imageId = imageID;
        this.resId = resId;
        this.search = search;
        this.path = path;
        this.count = count;
    }
    public ItemData(){}

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

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
