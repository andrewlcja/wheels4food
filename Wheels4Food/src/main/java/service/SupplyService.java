/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.ItemDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import model.CreateSupplyRequest;
import model.CreateSupplyResponse;
import model.DeleteSupplyResponse;
import model.Item;
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
    ItemDAO itemDAO;

    @Autowired
    SupplyDAO supplyDAO;

    public CreateSupplyResponse createSupplyRequest(CreateSupplyRequest request) {
        int userID = request.getUserID();
        int itemID = request.getItemID();
        String quantitySuppliedStr = request.getQuantitySupplied().trim();
        String expiryDate = request.getExpiryDate().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (userID <= 0) {
            errorList.add("Invalid user id");
        }

        if (itemID <= 0) {
            errorList.add("Invalid item id");
        }

        if (quantitySuppliedStr.equals("")) {
            errorList.add("Quantity cannot be blank");
        }

        if (expiryDate.equals("")) {
            errorList.add("Expiry Date cannot be blank");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateSupplyResponse(false, errorList);
        }

        try {
            int quantitySupplied = Integer.parseInt(quantitySuppliedStr);

            if (quantitySupplied <= 0) {
                errorList.add("Quantity must be more than 0");
            }

            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
            String datePosted = sdf.format(today);

            try {
                Date expiry = sdf.parse(expiryDate);

                if (today.compareTo(expiry) >= 0) {
                    errorList.add("Expiry Date must be a date after today");
                    return new CreateSupplyResponse(false, errorList);
                }

                try {
                    User user = userDAO.getUserById(userID);
                    if (user == null) {
                        errorList.add("Invalid user id");
                    }

                    Item item = itemDAO.getItemById(itemID);
                    if (item == null) {
                        errorList.add("Invalid item id");
                    }

                    //check if the errorlist is empty
                    if (!errorList.isEmpty()) {
                        return new CreateSupplyResponse(false, errorList);
                    }

                    supplyDAO.createSupply(new Supply(user, item, quantitySupplied, expiryDate, datePosted));
                    return new CreateSupplyResponse(true, null);
                } catch (Exception e) {
                    errorList.add(e.getMessage());
                    return new CreateSupplyResponse(false, errorList);
                }
            } catch (ParseException e) {
                errorList.add("Invalid Expiry Date");
                return new CreateSupplyResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Quantity must be an integer");
            return new CreateSupplyResponse(false, errorList);
        }
    }

    public UpdateSupplyResponse updateSupplyRequest(Supply supply) {
        int quantitySupplied = supply.getQuantitySupplied();
        String expiryDate = supply.getExpiryDate().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (quantitySupplied <= 0) {
            errorList.add("Quantity must be more than 0");
        }

        if (expiryDate.equals("")) {
            errorList.add("Expiry Date cannot be blank");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new UpdateSupplyResponse(false, errorList);
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

            try {
                supplyDAO.updateSupply(supply);
                return new UpdateSupplyResponse(true, errorList);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new UpdateSupplyResponse(false, errorList);
            }
        } catch (ParseException e) {
            errorList.add("Invalid Expiry Date");
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
}
