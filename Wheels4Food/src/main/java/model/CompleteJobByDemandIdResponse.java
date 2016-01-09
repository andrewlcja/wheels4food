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
public class CompleteJobByDemandIdResponse {
    private boolean isCompleted;
    private ArrayList<String> errorList;

    public CompleteJobByDemandIdResponse(boolean isCompleted, ArrayList<String> errorList) {
        this.isCompleted = isCompleted;
        this.errorList = errorList;
    }

    public boolean isIsCompleted() {
        return isCompleted;
    }
    
    public ArrayList<String> getErrorList() {
        return errorList;
    }
}
