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
import model.DemandItem;
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
        String sku = request.getSku().trim();
        String itemName = request.getItemName().trim();
        String category = request.getCategory().trim();
        String unit = request.getUnit().trim();
        String quantitySuppliedStr = request.getQuantitySupplied().trim();
        String minimumStr = request.getMinimum().trim();
        String maximumStr = request.getMaximum().trim();
        String expiryDate = request.getExpiryDate().trim();
        String monetaryValueStr = request.getMonetaryValue().trim();

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
        
        if (unit.equals("")) {
            errorList.add("Unit of Measurement cannot be blank");
        }

        if (quantitySuppliedStr.equals("")) {
            errorList.add("Quantity Supplied cannot be blank");
        }

        if (minimumStr.equals("")) {
            errorList.add("Minimmum Request Quantity cannot be blank");
        }

        if (maximumStr.equals("")) {
            maximumStr = quantitySuppliedStr;
        }

        if (expiryDate.equals("")) {
            errorList.add("Expiry Date cannot be blank");
        }
        
        if (monetaryValueStr.equals("")) {
            errorList.add("Monetary Value cannot be blank");
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
                errorList.add("Minimum Request Quantity must be more than 0");
            }
        } catch (NumberFormatException e) {
            errorList.add("Minimum Request Quantity must be an integer");
        }

        int maximum = 0;
        try {
            maximum = Integer.parseInt(maximumStr);

            if (maximum <= 0) {
                errorList.add("Maximum Request Quantity must be more than 0");
            }
        } catch (NumberFormatException e) {
            errorList.add("Maximum Request Quantity must be an integer");
        }
        
        float monetaryValue = 0;
        try {
            monetaryValue = Float.parseFloat(monetaryValueStr);

            if (maximum <= 0) {
                errorList.add("Monetary Value must be more than 0");
            }
        } catch (NumberFormatException e) {
            errorList.add("Monetary Value must be an number");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateSupplyResponse(false, errorList);
        }

        //further validation on quantities
        if (minimum > quantitySupplied) {
            errorList.add("Minimum Request Quantity cannot be more than the Quantity Supplied");
        }

        if (minimum > maximum) {
            errorList.add("Minimum Request Quantity cannot be more than the Maximum Quantity");
        }

        if (maximum > quantitySupplied) {
            errorList.add("Maximum Request Quantity cannot be more than the Quantity Supplied");
        }

        if (maximum != quantitySupplied && (maximum + minimum) > quantitySupplied) {
            errorList.add("Maximum Request Quantity must be either lesser than equals to " + (quantitySupplied - minimum) + " or equals to " + quantitySupplied);
        }

        //generate date posted
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String datePosted = sdf.format(today);

        try {
            if (!expiryDate.equals("NA")) {
                Date expiry = sdf.parse(expiryDate);

                //date validations
                if (today.compareTo(expiry) >= 0) {
                    errorList.add("Expiry Date must be a date after today");
                }
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

            supplyDAO.createSupply(new Supply(user, sku, itemName, category, unit, quantitySupplied, minimum, maximum, maximum, expiryDate, monetaryValue, datePosted));
            return new CreateSupplyResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreateSupplyResponse(false, errorList);
        }
    }

    public UpdateSupplyResponse updateSupplyRequest(Supply supply) {
        String sku = supply.getSku().trim();
        String itemName = supply.getItemName().trim();
        String category = supply.getCategory().trim();
        String unit = supply.getUnit().trim();
        int quantitySupplied = supply.getQuantitySupplied();
        int minimum = supply.getMinimum();
        int maximum = supply.getMaximum();
        String expiryDate = supply.getExpiryDate().trim();
        float monetaryValue = supply.getMonetaryValue();

        ArrayList<String> errorList = new ArrayList<String>();
        
        if (itemName.equals("")) {
            errorList.add("Item Name cannot be blank");
        }

        if (category.equals("")) {
            errorList.add("Category cannot be blank");
        }
        
        if (unit.equals("")) {
            errorList.add("Unit of Measurement cannot be blank");
        }

        if (quantitySupplied <= 0) {
            errorList.add("Quantity Supplied must be more than 0");
        }

        if (minimum <= 0) {
            errorList.add("Minimum Request Quantity must be more than 0");
        }

        if (maximum <= 0) {
            errorList.add("Maximum Request Quantity must be more than 0");
        }

        if (expiryDate.equals("")) {
            errorList.add("Expiry Date cannot be blank");
        }
        
        if (monetaryValue < 0) {
            errorList.add("Monetary Value must be more than equals to 0");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new UpdateSupplyResponse(false, errorList);
        }

        //further validation on quantities
        if (minimum > quantitySupplied) {
            errorList.add("Minimum Request Quantity cannot be more than the Quantity Supplied");
        }

        if (minimum > maximum) {
            errorList.add("Minimum Request Quantity cannot be more than the Maximum Quantity");
        }

        if (maximum > quantitySupplied) {
            errorList.add("Maximum Request Quantity cannot be more than the Quantity Supplied");
        }

        if (maximum != quantitySupplied && (maximum + minimum) > quantitySupplied) {
            errorList.add("Maximum Request Quantity must be either lesser than equals to " + (quantitySupplied - minimum) + " or equals to " + quantitySupplied);
        }

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        try {
            if (!expiryDate.equals("NA")) {
                Date expiry = sdf.parse(expiryDate);

                if (today.compareTo(expiry) >= 0) {
                    errorList.add("Expiry Date must be a date after today");
                    return new UpdateSupplyResponse(false, errorList);
                }
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
                List<DemandItem> demandItemList = demandDAO.getDemandItemListBySupplyId(id);
                if (!demandItemList.isEmpty()) {
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

    public List<Supply> getSupplyListByCategoryRequest(String category) throws Exception {
        return supplyDAO.getSupplyListByCategory(category);
    }

    public Supply getSupplyByIdRequest(int id) throws Exception {
        return supplyDAO.getSupplyById(id);
    }
}
