package com.example.neps.tabhostgridviewexample;

import android.graphics.Bitmap;


//그리드뷰에서 이미지 클릭시 클릭을 당하는 객체가 가지고 있을 정보.

public class ImageItem {
    private Bitmap image;
    private String title;

    public ImageItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {return title;}
    public void setTitle(String title) { this.title = title; }
}
