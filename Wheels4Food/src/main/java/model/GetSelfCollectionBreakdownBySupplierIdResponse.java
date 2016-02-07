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
public class GetSelfCollectionBreakdownBySupplierIdResponse {
    private int pending;
    private int cancelled;
    private int completed;

    public GetSelfCollectionBreakdownBySupplierIdResponse(int pending, int cancelled, int completed) {
        this.pending = pending;
        this.cancelled = cancelled;
        this.completed = completed;
    }

    public int getPending() {
        return pending;
    }

    public int getCancelled() {
        return cancelled;
    }

    public int getCompleted() {
        return completed;
    }
}
