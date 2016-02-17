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
public class CreateJobRequest {
    private int demandID;
    private int userID;
    private String deliveryDate;
    private String timeslot;
    

    public CreateJobRequest() {
    }

    public CreateJobRequest(int demandID, int userID, String deliveryDate, String timeslot) {
        this.demandID = demandID;
        this.userID = userID;
        this.deliveryDate = deliveryDate;
        this.timeslot = timeslot;
    }

    public int getDemandID() {
        return demandID;
    }

    public int getUserID() {
        return userID;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getTimeslot() {
        return timeslot;
    }
}
