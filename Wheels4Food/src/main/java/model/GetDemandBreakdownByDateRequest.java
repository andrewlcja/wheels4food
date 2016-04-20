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
public class GetDemandBreakdownByDateRequest {
    private int id;
    private int startMonth;
    private int endMonth;
    private int year;

    public GetDemandBreakdownByDateRequest() {
    }

    public GetDemandBreakdownByDateRequest(int id, int startMonth, int endMonth, int year) {
        this.id = id;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public int getYear() {
        return year;
    }
}
