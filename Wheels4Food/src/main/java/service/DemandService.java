/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.util.ArrayList;
import java.util.List;
import model.ApproveDemandResponse;
import model.CreateDemandRequest;
import model.CreateDemandResponse;
import model.DeleteDemandResponse;
import model.Demand;
import model.Supply;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class DemandService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    SupplyDAO supplyDAO;

    @Autowired
    DemandDAO demandDAO;

    public CreateDemandResponse createDemandRequest(CreateDemandRequest request) {
        int userID = request.getUserID();
        int supplyID = request.getSupplyID();
        String quantityDemandedStr = request.getQuantityDemanded().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (userID <= 0) {
            errorList.add("Invalid user id");
        }

        if (supplyID <= 0) {
            errorList.add("Invalid supply id");
        }

        if (quantityDemandedStr.equals("")) {
            errorList.add("Quantity requested cannot be blank");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateDemandResponse(false, errorList);
        }

        try {
            int quantityDemanded = Integer.parseInt(quantityDemandedStr);

            if (quantityDemanded <= 0) {
                errorList.add("Quantity requested must be more than 0");
            }

            try {
                User user = userDAO.getUserById(userID);
                if (user == null) {
                    errorList.add("Invalid user id");
                }

                Supply supply = supplyDAO.getSupplyById(supplyID);
                if (supply == null) {
                    errorList.add("Invalid supply id");
                }

                //check if the errorlist is empty
                if (!errorList.isEmpty()) {
                    return new CreateDemandResponse(false, errorList);
                }

                //get min and max of supply
                int minimum = supply.getMinimum();
                int maximum = supply.getMaximum();

                int quantityRemaining = supply.getQuantityRemaining();

                //check if item is still in stock
                if (quantityRemaining == 0) {
                    errorList.add("Unfortunately, the requested item is out of stock");
                    return new CreateDemandResponse(false, errorList);
                }

                //check if quantity demanded is in the valid range
                if ((quantityRemaining - minimum < minimum) && quantityDemanded != quantityRemaining) {
                    errorList.add("Quantity requested must be equals to " + quantityRemaining);
                } else if ((minimum + maximum) > quantityRemaining && (quantityRemaining - quantityDemanded) < minimum && (quantityRemaining - quantityDemanded) != 0) {
                    errorList.add("Quantity requested must be more than equals to " + minimum + " and less than equals to " + (quantityRemaining - minimum) + " OR equals to " + quantityRemaining);
                } else if (quantityDemanded > maximum || quantityDemanded < minimum) {
                    errorList.add("Quantity requested must be more than equals to " + minimum + " and less than equals to " + maximum);
                }

                //check if the errorlist is empty
                if (!errorList.isEmpty()) {
                    return new CreateDemandResponse(false, errorList);
                }

                //set quantity remaining
                supply.setQuantityRemaining(quantityRemaining - quantityDemanded);

                //check if there's a need to update the maximum limit
                if (maximum > supply.getQuantityRemaining()) {
                    supply.setMaximum(supply.getQuantityRemaining());
                } else if (supply.getQuantityRemaining() - maximum < minimum) {
                    supply.setMaximum(supply.getQuantityRemaining() - minimum);
                }

                supplyDAO.updateSupply(supply);

                demandDAO.createDemand(new Demand(user, supply, quantityDemanded, "Pending"));
                return new CreateDemandResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new CreateDemandResponse(false, errorList);
            }

        } catch (NumberFormatException e) {
            errorList.add("Quantity must be an integer");
            return new CreateDemandResponse(false, errorList);
        }
    }

    public DeleteDemandResponse deleteDemandRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new DeleteDemandResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                Demand demand = demandDAO.getDemandById(id);
                Supply supply = demand.getSupply();

                if (demand.getStatus().equals("Pending")) {
                    //add back the quantity demanded
                    int newQuantityRemaining = supply.getQuantityRemaining() + demand.getQuantityDemanded();
                    supply.setQuantityRemaining(newQuantityRemaining);
                    supplyDAO.updateSupply(supply);
                }

                demandDAO.deleteDemand(id);
                return new DeleteDemandResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new DeleteDemandResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new DeleteDemandResponse(false, errorList);
        }
    }

    public ApproveDemandResponse approveDemandRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new ApproveDemandResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);
            
            Demand demand = demandDAO.getDemandById(id);
            demand.setStatus("Approved");
            
            try {
                demandDAO.updateDemand(demand);
                return new ApproveDemandResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new ApproveDemandResponse(false, errorList); 
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new ApproveDemandResponse(false, errorList);
        }
    }

    public List<Demand> getDemandListByUserIdRequest(int userID) throws Exception {
        return demandDAO.getDemandListByUserId(userID);
    }

    public List<Demand> getPendingDemandListBySupplierIdRequest(int supplierID) throws Exception {
        return demandDAO.getPendingDemandListBySupplierId(supplierID);
    }
}
