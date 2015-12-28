/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import model.ApproveDemandRequest;
import model.ApproveDemandResponse;
import model.CreateDemandRequest;
import model.CreateDemandResponse;
import model.DeleteDemandResponse;
import model.Demand;
import model.RejectDemandRequest;
import model.RejectDemandResponse;
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
                } else if (quantityRemaining - minimum == minimum && quantityDemanded != quantityRemaining && quantityDemanded != minimum) {
                    errorList.add("Quantity requested must be equals to " + minimum + " OR equals to " + maximum);
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

                //generate date requested
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                String dateRequested = sdf.format(today);

                demandDAO.createDemand(new Demand(user, supply, quantityDemanded, dateRequested, "Pending", ""));
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

                    //check initial maximum limit
                    int initialMaximum = supply.getInitialMaximum();
                    int minimum = supply.getMinimum();

                    if (initialMaximum + minimum <= newQuantityRemaining || initialMaximum == newQuantityRemaining) {
                        supply.setMaximum(initialMaximum);
                    } else {
                        supply.setMaximum(newQuantityRemaining - minimum);
                    }

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

    public ApproveDemandResponse approveDemandRequest(String idString, ApproveDemandRequest request) {
        ArrayList<String> errorList = new ArrayList<String>();

        String comments = request.getComments();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new ApproveDemandResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            Demand demand = demandDAO.getDemandById(id);
            String status = demand.getStatus();

            if (!status.equals("Pending")) {
                errorList.add("Status of request must be pending");
                return new ApproveDemandResponse(false, errorList);
            }

            demand.setStatus("Approved");

            Supply supply = demand.getSupply();
            int quantitySupplied = supply.getQuantitySupplied();
            int quantityDemanded = demand.getQuantityDemanded();

            if (quantityDemanded <= quantitySupplied) {
                supply.setQuantitySupplied(quantitySupplied - quantityDemanded);
            } else {
                if (comments.equals("")) {
                    errorList.add("Comments cannot be blank");
                    return new ApproveDemandResponse(false, errorList);
                }

                demand.setComments(comments);
                supply.setQuantitySupplied(0);
            }

            try {
                supplyDAO.updateSupply(supply);
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

    public RejectDemandResponse rejectDemandRequest(String idString, RejectDemandRequest request) {
        ArrayList<String> errorList = new ArrayList<String>();

        String comments = request.getComments();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new RejectDemandResponse(false, errorList);
        }

        if (comments.equals("")) {
            errorList.add("Comments cannot be blank");
            return new RejectDemandResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            Demand demand = demandDAO.getDemandById(id);
            String status = demand.getStatus();
            Supply supply = demand.getSupply();

            if (!status.equals("Pending")) {
                errorList.add("Status of request must be pending");
                return new RejectDemandResponse(false, errorList);
            }

            //add back the quantity demanded
            int newQuantityRemaining = supply.getQuantityRemaining() + demand.getQuantityDemanded();
            supply.setQuantityRemaining(newQuantityRemaining);

            //check initial maximum limit
            int initialMaximum = supply.getInitialMaximum();
            int minimum = supply.getMinimum();

            if (initialMaximum + minimum <= newQuantityRemaining || initialMaximum == newQuantityRemaining) {
                supply.setMaximum(initialMaximum);
            } else {
                supply.setMaximum(newQuantityRemaining - minimum);
            }

            demand.setComments(comments);
            demand.setStatus("Rejected");

            try {
                supplyDAO.updateSupply(supply);
                demandDAO.updateDemand(demand);
                return new RejectDemandResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new RejectDemandResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new RejectDemandResponse(false, errorList);
        }
    }

    public List<Demand> getDemandListByUserIdRequest(int userID) throws Exception {
        return demandDAO.getDemandListByUserId(userID);
    }

    public List<Demand> getDemandListBySupplyIdRequest(int supplyID) throws Exception {
        return demandDAO.getDemandListBySupplyId(supplyID);
    }

    public List<Demand> getPendingDemandListBySupplierIdRequest(int supplierID) throws Exception {
        return demandDAO.getPendingDemandListBySupplierId(supplierID);
    }
}
