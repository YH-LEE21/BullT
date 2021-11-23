package com.example.bullt.Banners;

public class BannerData {
    private String imagePath="";
    private String ref="";


    public BannerData(String imagePath,String ref){
        this.imagePath = imagePath;
        this.ref = ref;
    }
    public BannerData(){}
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
