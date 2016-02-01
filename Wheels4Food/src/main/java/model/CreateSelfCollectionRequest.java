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
public class CreateSelfCollectionRequest {
    private int demandID;
    private String deliveryDate;
    private String timeslot;

    public CreateSelfCollectionRequest() {
    }

    public CreateSelfCollectionRequest(int demandID, String deliveryDate, String timeslot) {
        this.demandID = demandID;
        this.deliveryDate = deliveryDate;
        this.timeslot = timeslot;
    }

    public int getDemandID() {
        return demandID;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getTimeslot() {
        return timeslot;
    }
}
