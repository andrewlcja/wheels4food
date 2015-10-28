/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author andrew.lim.2013
 */
public class ApproveDemandResponse {
    private boolean isApproved;
    private ArrayList<String> errorList;

    public ApproveDemandResponse(boolean isApproved, ArrayList<String> errorList) {
        this.isApproved = isApproved;
        this.errorList = errorList;
    }

    public boolean isIsApproved() {
        return isApproved;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }
}
