package com.dzinesunlimited.resto.utils.helpers.pojos.backend;

public class PrinterCategoryData {

    private String printCatID;
    private String printerID;
    private String catID;
    private String catName;
    private boolean status;

    public String getPrintCatID() {
        return printCatID;
    }

    public void setPrintCatID(String printCatID) {
        this.printCatID = printCatID;
    }

    public String getPrinterID() {
        return printerID;
    }

    public void setPrinterID(String printerID) {
        this.printerID = printerID;
    }

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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}