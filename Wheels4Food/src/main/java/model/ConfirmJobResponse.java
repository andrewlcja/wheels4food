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
public class ConfirmJobResponse {
    private boolean isUpdated;
    private ArrayList<String> errorList;

    public ConfirmJobResponse(boolean isUpdated, ArrayList<String> errorList) {
        this.isUpdated = isUpdated;
        this.errorList = errorList;
    }

    public boolean isIsUpdated() {
        return isUpdated;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    } 
}
