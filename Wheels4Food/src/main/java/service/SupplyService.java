/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import model.CreateSupplyRequest;
import model.CreateSupplyResponse;
import model.DeleteSupplyResponse;
import model.Demand;
import model.Supply;
import model.UpdateSupplyResponse;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Wayne
 */
public class SupplyService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    SupplyDAO supplyDAO;

    @Autowired
    DemandDAO demandDAO;

    public CreateSupplyResponse createSupplyRequest(CreateSupplyRequest request) {
        int userID = request.getUserID();
        String itemName = request.getItemName().trim();
        String category = request.getCategory().trim();
        String quantitySuppliedStr = request.getQuantitySupplied().trim();
        String minimumStr = request.getMinimum().trim();
        String maximumStr = request.getMaximum().trim();
        String expiryDate = request.getExpiryDate().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (userID <= 0) {
            errorList.add("Invalid user id");
        }

        if (itemName.equals("")) {
            errorList.add("Item Name cannot be blank");
        }

        if (category.equals("")) {
            errorList.add("Category cannot be blank");
        }

        if (quantitySuppliedStr.equals("")) {
            errorList.add("Quantity Supplied cannot be blank");
        }

        if (minimumStr.equals("")) {
            errorList.add("Minimmum Quantity cannot be blank");
        }

        if (maximumStr.equals("")) {
            maximumStr = quantitySuppliedStr;
        }

        if (expiryDate.equals("")) {
            errorList.add("Expiry Date cannot be blank");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateSupplyResponse(false, errorList);
        }

        //validate integer inputs
        int quantitySupplied = 0;
        try {
            quantitySupplied = Integer.parseInt(quantitySuppliedStr);

            if (quantitySupplied <= 0) {
                errorList.add("Quantity Supplied must be more than 0");
            }
        } catch (NumberFormatException e) {
            errorList.add("Quantity Supplied must be an integer");
        }

        int minimum = 0;
        try {
            minimum = Integer.parseInt(minimumStr);

            if (minimum <= 0) {
                errorList.add("Minimum Quantity must be more than 0");
            }
        } catch (NumberFormatException e) {
            errorList.add("Minimum Quantity must be an integer");
        }

        int maximum = 0;
        try {
            maximum = Integer.parseInt(maximumStr);

            if (maximum <= 0) {
                errorList.add("Maximum Quantity must be more than 0");
            }
        } catch (NumberFormatException e) {
            errorList.add("Maximum Quantity must be an integer");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateSupplyResponse(false, errorList);
        }

        //further validation on quantities
        if (minimum > quantitySupplied) {
            errorList.add("Minimum Quantity cannot be more than the Quantity Supplied");
        }

        if (minimum > maximum) {
            errorList.add("Minimum Quantity cannot be more than the Maximum Quantity");
        }

        if (maximum > quantitySupplied) {
            errorList.add("Maximum Quantity cannot be more than the Quantity Supplied");
        }

        if (maximum != quantitySupplied && (maximum + minimum) > quantitySupplied) {
            errorList.add("Maximum Quantity must be either lesser than equals to " + (quantitySupplied - minimum) + " or equals to " + quantitySupplied);
        }

        //generate date posted
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String datePosted = sdf.format(today);

        try {
            Date expiry = sdf.parse(expiryDate);

            //date validations
            if (today.compareTo(expiry) >= 0) {
                errorList.add("Expiry Date must be a date after today");
            }
        } catch (ParseException e) {
            errorList.add("Invalid Expiry Date");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateSupplyResponse(false, errorList);
        }

        try {
            User user = userDAO.getUserById(userID);
            if (user == null) {
                errorList.add("Invalid user id");
                return new CreateSupplyResponse(false, errorList);
            }

            supplyDAO.createSupply(new Supply(user, itemName, category, quantitySupplied, quantitySupplied, minimum, maximum, maximum, expiryDate, datePosted));
            return new CreateSupplyResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreateSupplyResponse(false, errorList);
        }
    }

    public UpdateSupplyResponse updateSupplyRequest(Supply supply) {
        String itemName = supply.getItemName().trim();
        String category = supply.getCategory().trim();
        int quantitySupplied = supply.getQuantitySupplied();
        int quantityRemaining = supply.getQuantitySupplied();
        int minimum = supply.getMinimum();
        int maximum = supply.getMaximum();
        String expiryDate = supply.getExpiryDate().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (itemName.equals("")) {
            errorList.add("Item Name cannot be blank");
        }

        if (category.equals("")) {
            errorList.add("Category cannot be blank");
        }

        if (quantitySupplied <= 0) {
            errorList.add("Quantity Supplied must be more than 0");
        }

        if (minimum <= 0) {
            errorList.add("Minimum Quantity must be more than 0");
        }

        if (maximum <= 0) {
            errorList.add("Maximum Quantity must be more than 0");
        }

        if (expiryDate.equals("")) {
            errorList.add("Expiry Date cannot be blank");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new UpdateSupplyResponse(false, errorList);
        }

        //further validation on quantities
        if (minimum > quantitySupplied) {
            errorList.add("Minimum Quantity cannot be more than the Quantity Supplied");
        }

        if (minimum > maximum) {
            errorList.add("Minimum Quantity cannot be more than the Maximum Quantity");
        }

        if (maximum > quantitySupplied) {
            errorList.add("Maximum Quantity cannot be more than the Quantity Supplied");
        }

        if (maximum != quantitySupplied && (maximum + minimum) > quantitySupplied) {
            errorList.add("Maximum Quantity must be either lesser than equals to " + (quantitySupplied - minimum) + " or equals to " + quantitySupplied);
        }

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        try {
            Date expiry = sdf.parse(expiryDate);

            if (today.compareTo(expiry) >= 0) {
                errorList.add("Expiry Date must be a date after today");
                return new UpdateSupplyResponse(false, errorList);
            }
        } catch (ParseException e) {
            errorList.add("Invalid Expiry Date");
            return new UpdateSupplyResponse(false, errorList);
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new UpdateSupplyResponse(false, errorList);
        }

        try {
            if (quantitySupplied < quantityRemaining) {
                supply.setQuantityRemaining(quantitySupplied);
            }

            supplyDAO.updateSupply(supply);
            return new UpdateSupplyResponse(true, errorList);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new UpdateSupplyResponse(false, errorList);
        }
    }

    public DeleteSupplyResponse deleteSupplyRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new DeleteSupplyResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                //check if there are any demand for this supply
                List<Demand> demandList = demandDAO.getDemandListBySupplyId(id);
                if (!demandList.isEmpty()) {
                    errorList.add("There is existing demand for this item");
                    return new DeleteSupplyResponse(false, errorList);
                }

                supplyDAO.deleteSupply(id);
                return new DeleteSupplyResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new DeleteSupplyResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new DeleteSupplyResponse(false, errorList);
        }
    }

    public List<Supply> getSupplyListRequest() throws Exception {
        return supplyDAO.retrieveAll();
    }

    public List<Supply> getSupplyListByUserIdRequest(int userID) throws Exception {
        return supplyDAO.getSupplyListByUserId(userID);
    }
    
    public Supply getSupplyByIdRequest(int id) throws Exception {
        return supplyDAO.getSupplyById(id);
    }
}