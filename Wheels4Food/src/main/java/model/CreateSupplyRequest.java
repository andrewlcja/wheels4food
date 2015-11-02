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
    private String itemName;
    private String category;    
    private String quantitySupplied;
    private String minimum;
    private String maximum;
    private String expiryDate;

    public CreateSupplyRequest() {
    }

    public CreateSupplyRequest(int userID, String itemName, String category, String quantitySupplied, String minimum, String maximum, String expiryDate) {
        this.userID = userID;
        this.itemName = itemName;
        this.category = category;
        this.quantitySupplied = quantitySupplied;
        this.minimum = minimum;
        this.maximum = maximum;
        this.expiryDate = expiryDate;
    }

    public int getUserID() {
        return userID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
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
}
