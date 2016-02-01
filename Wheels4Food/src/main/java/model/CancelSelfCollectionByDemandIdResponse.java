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
public class CancelSelfCollectionByDemandIdResponse {
    private boolean isCancelled;
    private ArrayList<String> errorList;

    public CancelSelfCollectionByDemandIdResponse(boolean isCancelled, ArrayList<String> errorList) {
        this.isCancelled = isCancelled;
        this.errorList = errorList;
    }

    public boolean isIsCancelled() {
        return isCancelled;
    }
    
    public ArrayList<String> getErrorList() {
        return errorList;
    }
}
