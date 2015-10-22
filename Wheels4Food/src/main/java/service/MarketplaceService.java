/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.MarketplaceDAO;
import dao.UserDAO;
import java.util.*;
import model.CreateMarketplaceSupplyRequest;
import model.CreateMarketplaceSupplyResponse;
import model.Supply;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Wayne
 */
public class MarketplaceService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    MarketplaceDAO marketplaceDAO;

    public CreateMarketplaceSupplyResponse CreateMarketplaceSupplyRequest(CreateMarketplaceSupplyRequest request) {

        String username = request.getUsername().trim();
        String organizationName = request.getOrganizationName().trim();
        String itemName = request.getItemName().trim();
        String quantity = request.getQuantity().trim();
        String expiryDate = request.getExpiryDate().trim();
        String datePosted = request.getDatePosted().trim();
        String requester = request.getRequester().trim();
        String category = request.getCategory().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        java.util.Date date= new java.util.Date();

        //validations
        if (username.equals("")) {
            errorList.add("Username cannot be blank");
        }

        if (organizationName.equals("")) {
            errorList.add("Organization Name cannot be blank");
        }

        if (itemName.equals("")) {
            errorList.add("Item Name cannot be blank");
        }

        if (quantity.equals("")) {
            errorList.add("Quantity cannot be blank");
        }

        if (expiryDate.equals("")) {
            errorList.add("Expiry Date cannot be blank");
        }

        if (category.equals("")) {
            errorList.add("Category cannot be blank");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateMarketplaceSupplyResponse(false, errorList);
        }

        try {
            //create a new supply entry and return to marketplaceDAO to create in the table
            datePosted = date.toString();
            marketplaceDAO.createSupply(new Supply(username, organizationName, itemName, quantity, expiryDate, datePosted, requester, category));
            return new CreateMarketplaceSupplyResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreateMarketplaceSupplyResponse(false, errorList);
        }
    }
}
