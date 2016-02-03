package com.dzinesunlimited.resto.utils.helpers.pojos.backend;

public class TaxesData {

    private String taxID;
    private String taxName;
    private String taxPercentage;
    private String taxRegistration;
    private String taxCompleteAmount;
    private String taxPercentageAmount;

    public String getTaxID() {
        return taxID;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
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

    public String getTaxRegistration() {
        return taxRegistration;
    }

    public void setTaxRegistration(String taxRegistration) {
        this.taxRegistration = taxRegistration;
    }

    public String getTaxCompleteAmount() {
        return taxCompleteAmount;
    }

    public void setTaxCompleteAmount(String taxCompleteAmount) {
        this.taxCompleteAmount = taxCompleteAmount;
    }

    public String getTaxPercentageAmount() {
        return taxPercentageAmount;
    }

    public void setTaxPercentageAmount(String taxPercentageAmount) {
        this.taxPercentageAmount = taxPercentageAmount;
    }
}