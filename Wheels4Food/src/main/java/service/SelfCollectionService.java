/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.SelfCollectionDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.CancelSelfCollectionByDemandIdResponse;
import model.CompleteSelfCollectionByDemandIdResponse;
import model.CreateSelfCollectionRequest;
import model.CreateSelfCollectionResponse;
import model.Demand;
import model.Job;
import model.SelfCollection;
import model.Supply;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class SelfCollectionService {

    @Autowired
    SelfCollectionDAO selfCollectionDAO;

    @Autowired
    DemandDAO demandDAO;

    @Autowired
    SupplyDAO supplyDAO;

    @Autowired
    UserDAO userDAO;

    public CreateSelfCollectionResponse createSelfCollectionRequest(CreateSelfCollectionRequest request) {
        int demandID = request.getDemandID();
        String deliveryDateStr = request.getDeliveryDate();
        String timeslot = request.getTimeslot();

        ArrayList<String> errorList = new ArrayList<String>();

        if (demandID <= 0) {
            errorList.add("Invalid demand id.");
            return new CreateSelfCollectionResponse(false, errorList);
        }

        Demand demand = demandDAO.getDemandById(demandID);
        String status = demand.getStatus();

        if (deliveryDateStr.equals("")) {
            errorList.add("Delivery Date cannot be blank.");
        }

        if (timeslot.equals("")) {
            errorList.add("Timeslot cannot be blank.");
        }

        if (!status.equals("Pending")) {
            errorList.add("Status of request must be pending.");
            return new CreateSelfCollectionResponse(false, errorList);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date deliveryDate = sdf.parse(deliveryDateStr);
        } catch (ParseException e) {
            errorList.add("Invalid Delivery Date");
            return new CreateSelfCollectionResponse(false, errorList);
        }

        try {
            Supply supply = demand.getSupply();
            int quantitySupplied = supply.getQuantitySupplied();
            int quantityDemanded = demand.getQuantityDemanded();
            supply.setQuantitySupplied(quantitySupplied - quantityDemanded);
            
            demand.setStatus("Self Collection Created");
            
            supplyDAO.updateSupply(supply);
            demandDAO.updateDemand(demand);

            selfCollectionDAO.createSelfCollection(new SelfCollection(demand, deliveryDateStr, timeslot, "Active"));
            return new CreateSelfCollectionResponse(true, errorList);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreateSelfCollectionResponse(false, errorList);
        }
    }
    
    public SelfCollection getSelfCollectionByDemandIdRequest(int demandID) throws Exception {
        return selfCollectionDAO.getSelfCollectionByDemandId(demandID);
    }

    public CancelSelfCollectionByDemandIdResponse cancelSelfCollectionByDemandIdRequest(Demand demand) {
        int demandID = demand.getId();
        String comments = demand.getComments();

        ArrayList<String> errorList = new ArrayList<String>();

        if (demandID <= 0) {
            errorList.add("Invalid job id.");
        }

        if (comments.equals("")) {
            errorList.add("Reason cannot be blank.");
        }

        if (!errorList.isEmpty()) {
            return new CancelSelfCollectionByDemandIdResponse(false, errorList);
        }

        SelfCollection selfCollection = selfCollectionDAO.getSelfCollectionByDemandId(demandID);
        selfCollection.setStatus("Cancelled");

        demand.setStatus("Self Collection Cancelled");
        
        Supply supply = demand.getSupply();

        //add back the quantity demanded
        int newQuantityRemaining = supply.getQuantitySupplied() + demand.getQuantityDemanded();
        supply.setQuantitySupplied(newQuantityRemaining);

        //check initial maximum limit
        int initialMaximum = supply.getInitialMaximum();
        int minimum = supply.getMinimum();

        if (initialMaximum + minimum <= newQuantityRemaining || initialMaximum == newQuantityRemaining) {
            supply.setMaximum(initialMaximum);
        } else {
            supply.setMaximum(newQuantityRemaining - minimum);
        }

        try {
            selfCollectionDAO.updateSelfCollection(selfCollection);
            demandDAO.updateDemand(demand);
            supplyDAO.updateSupply(supply);
            return new CancelSelfCollectionByDemandIdResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CancelSelfCollectionByDemandIdResponse(false, errorList);
        }
    }
    
    public CompleteSelfCollectionByDemandIdResponse completeSelfCollectionByDemandIdRequest(int demandID) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (demandID <= 0) {
            errorList.add("Invalid demand id");
            return new CompleteSelfCollectionByDemandIdResponse(false, errorList);
        }

        SelfCollection selfCollection = selfCollectionDAO.getSelfCollectionByDemandId(demandID);
        selfCollection.setStatus("Completed");

        Demand demand = demandDAO.getDemandById(demandID);
        demand.setStatus("Self Collection Completed");

        try {
            selfCollectionDAO.updateSelfCollection(selfCollection);
            demandDAO.updateDemand(demand);
            return new CompleteSelfCollectionByDemandIdResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CompleteSelfCollectionByDemandIdResponse(false, errorList);
        }
    }
}
