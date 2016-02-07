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
public class GetDemandBreakdownResponse {
    private int pending;
    private int approved;
    private int rejected;

    public GetDemandBreakdownResponse(int pending, int approved, int rejected) {
        this.pending = pending;
        this.approved = approved;
        this.rejected = rejected;
    }

    public int getPending() {
        return pending;
    }

    public int getApproved() {
        return approved;
    }

    public int getRejected() {
        return rejected;
    }
}
