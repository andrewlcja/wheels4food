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
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private String comments;

    public CreateJobRequest() {
    }

    public CreateJobRequest(int demandID, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, String comments) {
        this.demandID = demandID;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.comments= comments;
    }

    public int getDemandID() {
        return demandID;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }
    
    public String getComments() {
        return comments;
    }
}
