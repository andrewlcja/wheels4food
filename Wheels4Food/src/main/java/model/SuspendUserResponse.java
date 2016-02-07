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
public class SuspendUserResponse {
    private boolean isSuspended;
    private ArrayList<String> errorList;

    public SuspendUserResponse(boolean isSuspended, ArrayList<String> errorList) {
        this.isSuspended = isSuspended;
        this.errorList = errorList;
    }

    public boolean isIsSuspended() {
        return isSuspended;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    } 
}
