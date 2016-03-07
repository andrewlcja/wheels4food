/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.NotificationDAO;
import dao.SelfCollectionDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import model.ApproveDemandResponse;
import model.CancelSelfCollectionByDemandIdResponse;
import model.CompleteSelfCollectionByDemandIdResponse;
import model.CreateSelfCollectionRequest;
import model.CreateSelfCollectionResponse;
import model.Demand;
import model.DemandItem;
import model.GetSelfCollectionBreakdownBySupplierIdResponse;
import model.Job;
import model.Notification;
import model.SelfCollection;
import model.Supply;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import utility.ConfigUtility;

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

    @Autowired
    NotificationDAO notificationDAO;

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
            List<DemandItem> demandItemList = demandDAO.getDemandItemListByDemandId(demand.getId());

            String requestContent = "";
            for (DemandItem demandItem : demandItemList) {
                Supply supply = demandItem.getSupply();
                int quantitySupplied = supply.getQuantitySupplied();
                int quantityDemanded = demandItem.getQuantityDemanded();

                supply.setQuantitySupplied(quantitySupplied - quantityDemanded);

                if (supply.getMaximum() < supply.getQuantitySupplied()) {
                    supply.setMaximum(supply.getQuantitySupplied());
                }

                supplyDAO.updateSupply(supply);
                requestContent += "<tr><td style='text-align:center;'>" + supply.getItemName() + "</td><td style='text-align:center;'>" + quantityDemanded + "</td></tr>";
            }

            demand.setStatus("Self Collection Created");
            demandDAO.updateDemand(demand);

            selfCollectionDAO.createSelfCollection(new SelfCollection(demand, deliveryDateStr, timeslot, "Active"));

            //get properties
            ConfigUtility config = new ConfigUtility();
            final String emailUsername = config.getProperty("email_username");
            final String emailPassword = config.getProperty("email_password");

            //send email
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(emailUsername, emailPassword);
                        }
                    });

            try {
                String recipient = demand.getUser().getEmail();

                String body = "<div>\n"
                        + "            <p>Dear " + demand.getUser().getOrganizationName() + ",</p><br/>\n"
                        + "            <p>Your request has been approved by <b>" + demand.getSupplier().getOrganizationName() + "</b>. Here are the approved request details:</p>\n"
                        + "            <table border='1'>"
                        + "                <tr>"
                        + "                    <th>Item Name</th>"
                        + "                    <th>Quantity Approved</th>"
                        + "                </tr>"
                        + requestContent
                        + "            </table>"
                        + "            <p>Please login <a href='http://apps.greentransformationlab.com/Wheels4Food/Login'>here</a> to view this approved request!</p><br/>\n"
                        + "            <p>Regards,</p>\n"
                        + "            <p>Wheels4Food Team</p>\n"
                        + "        </div>";
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailUsername));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Wheels4Food - Request Approved");
                message.setText(body, "UTF-8", "html");

                Transport.send(message);
            } catch (MessagingException e) {
                errorList.add(e.getMessage());
                return new CreateSelfCollectionResponse(false, errorList);
            }

            //create notification
            notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "Your requested item(s) have been <b>approved</b> by <b>" + demand.getSupplier().getOrganizationName() + ".</b> Click here to go to <b>My Inventory - Demand</b>."));

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

        try {
            List<DemandItem> demandItemList = demandDAO.getDemandItemListByDemandId(demandID);

            for (DemandItem demandItem : demandItemList) {
                Supply supply = demandItem.getSupply();

                //add back the quantity demanded
                int newQuantityRemaining = supply.getQuantitySupplied() + demandItem.getQuantityDemanded();
                supply.setQuantitySupplied(newQuantityRemaining);

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

            //update demerit points to responsible party
            User deductUser = null;

            if (comments.contains("Requesting")) {
                deductUser = demand.getUser();

                //create notification
                notificationDAO.createNotification(new Notification(demand.getSupplier(), "ApprovedRequests", "A self collection has been <b>cancelled</b> due to Requesting Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>Approved Requests</b>."));
                notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "A self collection has been <b>cancelled</b> due to Requesting Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>My Inventory - Demand</b>."));
            } else if (comments.contains("Supplying")) {
                deductUser = demand.getSupplier();

                //create notification
                notificationDAO.createNotification(new Notification(demand.getSupplier(), "ApprovedRequests", "A self collection has been <b>cancelled</b> due to Supplying Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>Approved Requests</b>."));
                notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "A self collection has been <b>cancelled</b> due to Supplying Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>My Inventory - Demand</b>."));
            }

            if (deductUser != null) {
                deductUser.setDemeritPoints(deductUser.getDemeritPoints() + 1);
            }

            selfCollectionDAO.updateSelfCollection(selfCollection);
            demandDAO.updateDemand(demand);
            userDAO.updateUser(deductUser);
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

            //create notification
            notificationDAO.createNotification(new Notification(demand.getSupplier(), "ApprovedRequests", "<b>" + demand.getUser().getOrganizationName() + "</b> has <b>completed</b> a self collection. Click here to go to <b>Approved Requests</b>."));

            return new CompleteSelfCollectionByDemandIdResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CompleteSelfCollectionByDemandIdResponse(false, errorList);
        }
    }

    public GetSelfCollectionBreakdownBySupplierIdResponse getSelfCollectionBreakdownBySupplierIdRequest(int supplierID) throws Exception {
        List<SelfCollection> selfCollectionList = selfCollectionDAO.getSelfCollectionListBySupplierId(supplierID);

        int pending = 0;
        int cancelled = 0;
        int completed = 0;

        for (SelfCollection selfCollection : selfCollectionList) {
            String status = selfCollection.getDemand().getStatus();

            switch (status) {
                case "Self Collection Created":
                    pending++;
                    break;
                case "Self Collection Cancelled":
                    cancelled++;
                    break;
                case "Self Collection Completed":
                    completed++;
                    break;
            }
        }

        return new GetSelfCollectionBreakdownBySupplierIdResponse(pending, cancelled, completed);
    }
}
