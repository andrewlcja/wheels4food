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
import model.CreateDemandRequest;
import model.CreateDemandResponse;
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
            errorList.add("Quantity cannot be blank");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateDemandResponse(false, errorList);
        }

        try {
            int quantityDemanded = Integer.parseInt(quantityDemandedStr);

            if (quantityDemanded <= 0) {
                errorList.add("Quantity must be more than 0");
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
                
                int quantitySupplied = supply.getQuantitySupplied();
                if (quantitySupplied < quantityDemanded) {
                    errorList.add("Quantity must be less than " + quantitySupplied);
                }
                
                //check if the errorlist is empty
                if (!errorList.isEmpty()) {
                    return new CreateDemandResponse(false, errorList);
                }
                
                //deduct quantity demanded
                supply.setQuantitySupplied(quantitySupplied - quantityDemanded);
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
}
