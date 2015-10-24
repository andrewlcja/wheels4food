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
    private int itemID;
    private String quantitySupplied;
    private String expiryDate;

    public CreateSupplyRequest() {
    }

    public CreateSupplyRequest(int userID, int itemID, String quantitySupplied, String expiryDate) {
        this.userID = userID;
        this.itemID = itemID;
        this.quantitySupplied = quantitySupplied;
        this.expiryDate = expiryDate;
    }

    public int getUserID() {
        return userID;
    }

    public int getItemID() {
        return itemID;
    }

    public String getQuantitySupplied() {
        return quantitySupplied;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
