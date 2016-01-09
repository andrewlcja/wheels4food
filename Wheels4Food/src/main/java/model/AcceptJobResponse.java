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
public class AcceptJobResponse {
    private boolean isAccepted;
    private ArrayList<String> errorList;

    public AcceptJobResponse(boolean isAccepted, ArrayList<String> errorList) {
        this.isAccepted = isAccepted;
        this.errorList = errorList;
    }

    public boolean isIsAccepted() {
        return isAccepted;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }
}
