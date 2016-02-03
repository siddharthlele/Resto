package com.dzinesunlimited.resto.utils.helpers.pojos;

public class CategoriesData {
    private String catID;
    private String catName;
    private byte[] catPicture;

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

    public byte[] getCatPicture() {
        return catPicture;
    }

    public void setCatPicture(byte[] catPicture) {
        this.catPicture = catPicture;
    }
}