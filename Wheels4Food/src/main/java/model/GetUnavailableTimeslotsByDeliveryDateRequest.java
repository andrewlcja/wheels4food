/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author andrew.lim.2013
 */
public class GetUnavailableTimeslotsByDeliveryDateRequest {
    private int supplierID;
    private String deliveryDate;

    public GetUnavailableTimeslotsByDeliveryDateRequest() {
    }    
    
    public GetUnavailableTimeslotsByDeliveryDateRequest(int supplierID, String deliveryDate) {
        this.supplierID = supplierID;
        this.deliveryDate = deliveryDate;
    }

    public int getSupplierID() {
        return supplierID;
    }   
   
    public String getDeliveryDate() {
        return deliveryDate;
    }
}
