package com.example.bullt.Data;

import java.util.HashMap;
import java.util.Map;

public class ItemData implements Comparable<ItemData>{

    //상표명,상품종류,가격,이미지,주소
    private String title;
    private String content;
    private int price;
    private String ref;
    private String imagePath;
    private String search;
    private Boolean like;



    private Map<String, Boolean> hearts = new HashMap<>();

    private String id;
    private int count=0;

    public ItemData(String title, String content, int price, String resId, String path, String imageID, int count,String search){

        this.title = title;
        this.content = content;
        this.price = price;
        this.id = imageID;
        this.ref = resId;
        this.search = search;
        this.imagePath = path;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Map<String, Boolean> getHearts() {
        return hearts;
    }

    public void setHearts(Map<String, Boolean> hearts) {
        this.hearts = hearts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }
    @Override
    public int compareTo(ItemData o) {
        if (o.count < count) {
            return 1;
        } else if (o.price > count) {
            return -1;
        }
        return 0;
    }

}
