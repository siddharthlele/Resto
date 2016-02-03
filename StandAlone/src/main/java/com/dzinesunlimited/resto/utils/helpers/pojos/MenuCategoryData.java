package com.dzinesunlimited.resto.utils.helpers.pojos;

import android.graphics.Bitmap;

public class MenuCategoryData {
    private String catID;
    private String catName;
    private Bitmap catThumb;

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public Bitmap getCatThumb() {
        return catThumb;
    }

    public void setCatThumb(Bitmap catThumb) {
        this.catThumb = catThumb;
    }
}