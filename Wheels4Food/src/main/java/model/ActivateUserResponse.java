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
public class ActivateUserResponse {
    private boolean isActivated;
    private ArrayList<String> errorList;

    public ActivateUserResponse(boolean isActivated, ArrayList<String> errorList) {
        this.isActivated = isActivated;
        this.errorList = errorList;
    }

    public boolean isIsActivated() {
        return isActivated;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    } 
}
