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
    private String schedule;
    private String comments;

    public CreateJobRequest() {
    }

    public CreateJobRequest(int demandID, String schedule, String comments) {
        this.demandID = demandID;
        this.schedule = schedule;
        this.comments = comments;
    }

    public int getDemandID() {
        return demandID;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getComments() {
        return comments;
    }
}
