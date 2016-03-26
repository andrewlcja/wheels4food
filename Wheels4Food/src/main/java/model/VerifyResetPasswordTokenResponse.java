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
public class VerifyResetPasswordTokenResponse {
    private boolean isVerified;
    private ArrayList<String> errorList;

    public VerifyResetPasswordTokenResponse(boolean isVerified, ArrayList<String> errorList) {
        this.isVerified = isVerified;
        this.errorList = errorList;
    }

    public boolean isIsVerified() {
        return isVerified;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    } 
}
