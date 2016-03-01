package com.dzinesunlimited.resto.utils.helpers.pojos.backend;

public class CategoryTaxesData {

    private String catTaxesID;
    private String catID;
    private String taxID;
    private boolean taxStatus;
    private String taxName;
    private String taxPercentage;

    public String getCatTaxesID() {
        return catTaxesID;
    }

    public void setCatTaxesID(String catTaxesID) {
        this.catTaxesID = catTaxesID;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getTaxID() {
        return taxID;
    }

    public void setTaxID(String taxID) {
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