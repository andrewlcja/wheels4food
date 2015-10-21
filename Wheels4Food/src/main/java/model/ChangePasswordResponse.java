/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author Wayne
 */
public class ChangePasswordResponse {
    private boolean isChanged;
    private ArrayList<String> errorList;

    public ChangePasswordResponse(boolean isChanged, ArrayList<String> errorList) {
        this.isChanged = isChanged;
        this.errorList = errorList;
    }

    public boolean isIsUpdated() {
        return isChanged;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }
}
