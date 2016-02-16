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
    private String supplyIDValues;
    private String quantityDemandedValues;
    private String preferredDeliveryDate;
    private String preferredTimeslot;
    private String preferredSchedule;   

    public CreateDemandRequest() {
    }

    public CreateDemandRequest(int userID, String supplyIDValues, String quantityDemandedValues, String preferredDeliveryDate, String preferredTimeslot, String preferredSchedule) {
        this.userID = userID;
        this.supplyIDValues = supplyIDValues;
        this.quantityDemandedValues = quantityDemandedValues;
        this.preferredDeliveryDate = preferredDeliveryDate;
        this.preferredTimeslot = preferredTimeslot;
        this.preferredSchedule = preferredSchedule;
    }    

    public int getUserID() {
        return userID;
    }

    public String getSupplyIDValues() {
        return supplyIDValues;
    }

    public String getQuantityDemandedValues() {
        return quantityDemandedValues;
    }

    public String getPreferredDeliveryDate() {
        return preferredDeliveryDate;
    }

    public String getPreferredTimeslot() {
        return preferredTimeslot;
    }

    public String getPreferredSchedule() {
        return preferredSchedule;
    }    
}
