/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Wayne
 */
public class CreateSupplyRequest {

    private int userID;
    private String sku;
    private String itemName;
    private String category;   
    private String unit;
    private String quantitySupplied;
    private String minimum;
    private String maximum;
    private String expiryDate;
    private String monetaryValue;

    public CreateSupplyRequest() {
    }

    public CreateSupplyRequest(int userID, String sku, String itemName, String category, String unit, String quantitySupplied, String minimum, String maximum, String expiryDate, String monetaryValue) {
        this.userID = userID;
        this.sku = sku;
        this.itemName = itemName;
        this.category = category;
        this.unit = unit;
        this.quantitySupplied = quantitySupplied;
        this.minimum = minimum;
        this.maximum = maximum;
        this.expiryDate = expiryDate;
        this.monetaryValue = monetaryValue;
    }

    public int getUserID() {
        return userID;
    }

    public String getSku() {
        return sku;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public String getQuantitySupplied() {
        return quantitySupplied;
    }

    public String getMinimum() {
        return minimum;
    }

    public String getMaximum() {
        return maximum;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getMonetaryValue() {
        return monetaryValue;
    }    
}
