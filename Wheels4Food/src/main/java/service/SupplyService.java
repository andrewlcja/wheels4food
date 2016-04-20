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
import utility.ConfigUtility;

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
        ConfigUtility config = new ConfigUtility();
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

        try {
            //validations
            if (userID <= 0) {
                errorList.add(config.getProperty("user_id_invalid"));
            }

            if (itemName.equals("")) {
                errorList.add(config.getProperty("item_name_blank"));
            }

            if (category.equals("")) {
                errorList.add(config.getProperty("category_blank"));
            }

            if (unit.equals("")) {
                errorList.add(config.getProperty("uom_blank"));
            }

            if (quantitySuppliedStr.equals("")) {
                errorList.add(config.getProperty("quantity_supplied_blank"));
            }

            if (minimumStr.equals("")) {
                errorList.add(config.getProperty("quantity_minimum_blank"));
            }

            if (maximumStr.equals("")) {
                maximumStr = quantitySuppliedStr;
            }

            if (expiryDate.equals("")) {
                errorList.add(config.getProperty("expiry_date_blank"));
            }

            if (monetaryValueStr.equals("")) {
                errorList.add(config.getProperty("monetary_value_blank"));
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
                    errorList.add(config.getProperty("quantity_supplied_minimum"));
                }
            } catch (NumberFormatException e) {
                errorList.add(config.getProperty("quantity_supplied_number"));
            }

            int minimum = 0;
            try {
                minimum = Integer.parseInt(minimumStr);

                if (minimum <= 0) {
                    errorList.add(config.getProperty("quantity_minimum_minimum"));
                }
            } catch (NumberFormatException e) {
                errorList.add(config.getProperty("quantity_minimum_number"));
            }

            int maximum = 0;
            try {
                maximum = Integer.parseInt(maximumStr);

                if (maximum <= 0) {
                    errorList.add(config.getProperty("quantity_maximum_minimum"));
                }
            } catch (NumberFormatException e) {
                errorList.add(config.getProperty("quantity_maximum_number"));
            }

            float monetaryValue = 0;
            try {
                monetaryValue = Float.parseFloat(monetaryValueStr);

                if (maximum <= 0) {
                    errorList.add(config.getProperty("monetary_value_minimum"));
                }
            } catch (NumberFormatException e) {
                errorList.add(config.getProperty("monetary_value_number"));
            }

            //check if the errorlist is empty
            if (!errorList.isEmpty()) {
                return new CreateSupplyResponse(false, errorList);
            }

            //further validation on quantities
            if (minimum > quantitySupplied) {
                errorList.add(config.getProperty("quantity_minimum_exceed_supply"));
            }

            if (minimum > maximum) {
                errorList.add(config.getProperty("quantity_minimum_exceed_maximum"));
            }

            if (maximum > quantitySupplied) {
                errorList.add(config.getProperty("quantity_maximum_exceed_supply"));
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
                        errorList.add(config.getProperty("expiry_date_today"));
                    }
                }
            } catch (ParseException e) {
                errorList.add(config.getProperty("expiry_date_invalid"));
            }

            //check if the errorlist is empty
            if (!errorList.isEmpty()) {
                return new CreateSupplyResponse(false, errorList);
            }

            User user = userDAO.getUserById(userID);
            if (user == null) {
                errorList.add(config.getProperty("user_id_invalid"));
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
        ConfigUtility config = new ConfigUtility();
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

        try {
            if (itemName.equals("")) {
                errorList.add(config.getProperty("item_name_blank"));
            }

            if (category.equals("")) {
                errorList.add(config.getProperty("category_blank"));
            }

            if (unit.equals("")) {
                errorList.add(config.getProperty("uom_blank"));
            }

            if (quantitySupplied <= 0) {
                errorList.add(config.getProperty("quantity_supplied_minimum"));
            }

            if (minimum <= 0) {
                errorList.add(config.getProperty("quantity_minimum_minimum"));
            }

            if (maximum <= 0) {
                errorList.add(config.getProperty("quantity_maximum_minimum"));
            }

            if (expiryDate.equals("")) {
                errorList.add(config.getProperty("expiry_date_blank"));
            }

            if (monetaryValue < 0) {
                errorList.add(config.getProperty("monetary_value_minimum"));
            }

            //check if the errorlist is empty
            if (!errorList.isEmpty()) {
                return new UpdateSupplyResponse(false, errorList);
            }

            //further validation on quantities
            if (minimum > quantitySupplied) {
                errorList.add(config.getProperty("quantity_minimum_exceed_supply"));
            }

            if (minimum > maximum) {
                errorList.add(config.getProperty("quantity_minimum_exceed_maximum"));
            }

            if (maximum > quantitySupplied) {
                errorList.add(config.getProperty("quantity_maximum_exceed_supply"));
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
                        errorList.add(config.getProperty("expiry_date_today"));
                        return new UpdateSupplyResponse(false, errorList);
                    }
                }
            } catch (ParseException e) {
                errorList.add(config.getProperty("expiry_date_invalid"));
                return new UpdateSupplyResponse(false, errorList);
            }

            //check if the errorlist is empty
            if (!errorList.isEmpty()) {
                return new UpdateSupplyResponse(false, errorList);
            }

            supplyDAO.updateSupply(supply);
            return new UpdateSupplyResponse(true, errorList);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new UpdateSupplyResponse(false, errorList);
        }
    }

    public DeleteSupplyResponse deleteSupplyRequest(String idString) {
        ConfigUtility config = new ConfigUtility();
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
                    errorList.add(config.getProperty("existing_demand"));
                    return new DeleteSupplyResponse(false, errorList);
                }

                supplyDAO.deleteSupply(id);
                return new DeleteSupplyResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new DeleteSupplyResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an number");
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
