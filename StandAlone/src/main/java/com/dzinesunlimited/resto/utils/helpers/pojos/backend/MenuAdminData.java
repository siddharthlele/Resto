package com.dzinesunlimited.resto.utils.helpers.pojos.backend;

import android.graphics.Bitmap;

public class MenuAdminData {
    private String mealNo;
    private String mealName;
    private String mealDescription;
    private Bitmap mealImage;
    private String mealPrice;
    private String mealCatID;
    private String mealType;

    public String getMealNo() {
        return mealNo;
    }

    public void setMealNo(String mealNo) {
        this.mealNo = mealNo;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public Bitmap getMealImage() {
        return mealImage;
    }

    public void setMealImage(Bitmap mealImage) {
        this.mealImage = mealImage;
    }

    public String getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(String mealPrice) {
        this.mealPrice = mealPrice;
    }

    public String getMealCatID() {
        return mealCatID;
    }

    public void setMealCatID(String mealCatID) {
        this.mealCatID = mealCatID;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}