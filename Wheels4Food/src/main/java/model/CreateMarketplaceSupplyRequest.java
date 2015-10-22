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
public class CreateMarketplaceSupplyRequest {
    private String username;
    private String organizationName;
    private String itemName;
    private String quantity;
    private String expiryDate;
    private String supplyDatePosted;
    private String category;    

    public CreateMarketplaceSupplyRequest() {
    }

    public CreateMarketplaceSupplyRequest(String username, String organizationName, String itemName, String quantity, String expiryDate, String supplyDatePosted, String category) {
        this.username = username;
        this.organizationName = organizationName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.supplyDatePosted = supplyDatePosted;
        this.category = category;
    }

    public String getUsername() {
        return username;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getsupplyDatePosted() {
        return supplyDatePosted;
    }
    
    public String getCategory() {
        return category;
    }

    
}
