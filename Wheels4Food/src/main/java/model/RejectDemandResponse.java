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
public class RejectDemandResponse {
    private boolean isRejected;
    private ArrayList<String> errorList;

    public RejectDemandResponse(boolean isRejected, ArrayList<String> errorList) {
        this.isRejected = isRejected;
        this.errorList = errorList;
    }

    public boolean isIsRejected() {
        return isRejected;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }
}
