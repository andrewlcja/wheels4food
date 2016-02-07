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
public class GetJobBreakdownBySupplierIdResponse {

    private int pending;
    private int accepted;
    private int cancelled;
    private int completed;

    public GetJobBreakdownBySupplierIdResponse(int pending, int accepted, int cancelled, int completed) {
        this.pending = pending;
        this.accepted = accepted;
        this.cancelled = cancelled;
        this.completed = completed;
    }

    public int getPending() {
        return pending;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getCancelled() {
        return cancelled;
    }

    public int getCompleted() {
        return completed;
    }
}
