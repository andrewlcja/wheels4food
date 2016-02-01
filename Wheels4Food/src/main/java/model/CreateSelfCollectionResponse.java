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
public class CreateSelfCollectionResponse {
    private boolean isCreated;
    private ArrayList<String> errorList;

    public CreateSelfCollectionResponse(boolean isCreated, ArrayList<String> errorList) {
        this.isCreated = isCreated;
        this.errorList = errorList;
    }

    public boolean isIsCreated() {
        return isCreated;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }
}
