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
public class ResetPasswordResponse {
    private boolean isReset;
    private ArrayList<String> errorList;

    public ResetPasswordResponse(boolean isReset, ArrayList<String> errorList) {
        this.isReset = isReset;
        this.errorList = errorList;
    }

    public boolean isIsReset() {
        return isReset;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    } 
}
