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
    private String preferredDeliveryDate;
    private String preferredTimeslot;
    private String preferredSchedule;   

    public CreateDemandRequest() {
    }

    public CreateDemandRequest(int userID, int supplyID, String quantityDemanded, String preferredDeliveryDate, String preferredTimeslot, String preferredSchedule) {
        this.userID = userID;
        this.supplyID = supplyID;
        this.quantityDemanded = quantityDemanded;
        this.preferredDeliveryDate = preferredDeliveryDate;
        this.preferredTimeslot = preferredTimeslot;
        this.preferredSchedule = preferredSchedule;
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
