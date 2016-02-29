package com.dzinesunlimited.resto.utils.helpers.pojos.backend;

public class CategoryTaxesData {

    private int catTaxesID;
    private int catID;
    private int taxID;
    private boolean taxStatus;
    private String taxName;
    private String taxPercentage;

    public int getCatTaxesID() {
        return catTaxesID;
    }

    public void setCatTaxesID(int catTaxesID) {
        this.catTaxesID = catTaxesID;
    }

    public int getCatID() {
        return catID;
    }

    public void setCatID(int catID) {
        this.catID = catID;
    }

    public int getTaxID() {
        return taxID;
    }

    public void setTaxID(int taxID) {
        this.taxID = taxID;
    }

    public boolean isTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(boolean taxStatus) {
        this.taxStatus = taxStatus;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }
}