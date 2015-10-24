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
public class DeleteSupplyResponse {
    private boolean isDeleted;
    private ArrayList<String> errorList;

    public DeleteSupplyResponse(boolean isDeleted, ArrayList<String> errorList) {
        this.isDeleted = isDeleted;
        this.errorList = errorList;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    } 
}
