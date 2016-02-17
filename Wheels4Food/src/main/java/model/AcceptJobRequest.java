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
public class AcceptJobRequest {
    private int jobID;
    private int userID;
    private String collectionTime;
    private String deliveryTime;

    public AcceptJobRequest() {
    }

    public AcceptJobRequest(int jobID, int userID, String collectionTime, String deliveryTime) {
        this.jobID = jobID;
        this.userID = userID;
        this.collectionTime = collectionTime;
        this.deliveryTime = deliveryTime;
    }

    public int getJobID() {
        return jobID;
    }
    
    public int getUserID() {
        return userID;
    }

    public String getCollectionTime() {
        return collectionTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }
}
