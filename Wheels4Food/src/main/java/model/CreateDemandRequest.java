/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author andrew.lim.2013
 */
public class CreateDemandRequest {

    private int userID;
    private int supplyID;
    private String quantityDemanded;

    public CreateDemandRequest() {
    }

    public CreateDemandRequest(int userID, int supplyID, String quantityDemanded) {
        this.userID = userID;
        this.supplyID = supplyID;
        this.quantityDemanded = quantityDemanded;
    }

    public int getUserID() {
        return userID;
    }

    public int getSupplyID() {
        return supplyID;
    }

    public String getQuantityDemanded() {
        return quantityDemanded;
    }
}
