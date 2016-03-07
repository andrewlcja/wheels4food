/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.NotificationDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import model.ApproveDemandRequest;
import model.ApproveDemandResponse;
import model.ApprovePendingRegistrationResponse;
import model.CreateDemandRequest;
import model.CreateDemandResponse;
import model.DeleteDemandResponse;
import model.Demand;
import model.DemandItem;
import model.GetDemandBreakdownResponse;
import model.GetUnavailableTimeslotsByDeliveryDateRequest;
import model.Notification;
import model.RejectDemandRequest;
import model.RejectDemandResponse;
import model.Supply;
import model.UpdateDemandRequest;
import model.UpdateDemandResponse;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import utility.ConfigUtility;

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

    @Autowired
    NotificationDAO notificationDAO;

    public CreateDemandResponse createDemandRequest(CreateDemandRequest request) {
        int userID = request.getUserID();

        String[] supplyIDValues = new String[1];
        String supplyIDValuesStr = request.getSupplyIDValues().trim();
        if (supplyIDValuesStr.contains(",")) {
            supplyIDValues = supplyIDValuesStr.split(",");
        } else {
            supplyIDValues[0] = supplyIDValuesStr;
        }

        String[] quantityDemandedValues = new String[1];
        String quantityDemandedValuesStr = request.getQuantityDemandedValues().trim();
        if (quantityDemandedValuesStr.contains(",")) {
            quantityDemandedValues = quantityDemandedValuesStr.split(",");
        } else {
            quantityDemandedValues[0] = quantityDemandedValuesStr;
        }

        String preferredDeliveryDateStr = request.getPreferredDeliveryDate();
        String preferredTimeslot = request.getPreferredTimeslot();
        String preferredSchedule = request.getPreferredSchedule();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (userID <= 0) {
            errorList.add("Invalid user id");
        }

        for (String quantityDemandedValue : quantityDemandedValues) {
            if (quantityDemandedValue.equals("")) {
                errorList.add("All Quantity Requested fields cannot be blank.");
                break;
            }
        }

        if (preferredDeliveryDateStr.equals("")) {
            errorList.add("Preferred Delivery Date cannot be blank.");
        }

        if (preferredTimeslot.equals("")) {
            errorList.add("Preferred Timeslot requested cannot be blank.");
        }

        if (preferredSchedule.equals("")) {
            errorList.add("Preferred Schedule cannot be blank.");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateDemandResponse(false, errorList);
        }

        try {
            int[] quantityDemandedIntValues = new int[quantityDemandedValues.length];

            //check for valid quantity demanded values
            for (int i = 0; i < quantityDemandedIntValues.length; i++) {
                try {
                    int quantityDemanded = Integer.parseInt(quantityDemandedValues[i]);

                    if (quantityDemanded <= 0) {
                        errorList.add("All Quantity Requested fields must be an integer that is more than 0.");
                        return new CreateDemandResponse(false, errorList);
                    }
                    quantityDemandedIntValues[i] = quantityDemanded;
                } catch (NumberFormatException ex) {
                    errorList.add("All Quantity Requested fields must be an integer that is more than 0.");
                    return new CreateDemandResponse(false, errorList);
                }
            }

            if (preferredSchedule.equals("NA")) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date today = cal.getTime();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    Date preferredDeliveryDate = sdf.parse(preferredDeliveryDateStr);

                    if (today.compareTo(preferredDeliveryDate) >= 0 || (preferredDeliveryDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24) < 3) {
                        errorList.add("Preferred Delivery Date must be a date at least 3 days after today");
                        return new CreateDemandResponse(false, errorList);
                    }
                } catch (ParseException e) {
                    errorList.add("Invalid Preferred Delivery Date");
                }
            } else {
                if (StringUtils.countOccurrencesOf(preferredSchedule, "1") < 3) {
                    errorList.add("A minimum of 3 timeslots must be selected.");
                }
            }

            try {
                User user = userDAO.getUserById(userID);
                if (user == null) {
                    errorList.add("Invalid user id");
                }

                //check if the errorlist is empty
                if (!errorList.isEmpty()) {
                    return new CreateDemandResponse(false, errorList);
                }

                ArrayList<Supply> supplyList = new ArrayList<Supply>();

                for (int i = 0; i < supplyIDValues.length; i++) {
                    Supply supply = supplyDAO.getSupplyById(Integer.parseInt(supplyIDValues[i]));

                    //get min and max of supply
                    int minimum = supply.getMinimum();
                    int maximum = supply.getMaximum();

                    int quantitySupplied = supply.getQuantitySupplied();

                    int quantityDemanded = quantityDemandedIntValues[i];

                    //check if quantity demanded is in the valid range
                    if ((quantitySupplied - minimum < minimum) && quantityDemanded != quantitySupplied) {
                        errorList.add("Quantity requested for '" + supply.getItemName() + "' must be equals to " + quantitySupplied);
                    } else if (quantitySupplied - minimum == minimum && quantityDemanded != quantitySupplied && quantityDemanded != minimum) {
                        errorList.add("Quantity requested for '" + supply.getItemName() + "'  must be equals to " + minimum + " OR equals to " + maximum);
                    } else if ((minimum + maximum) > quantitySupplied && (quantitySupplied - quantityDemanded) < minimum && (quantitySupplied - quantityDemanded) != 0) {
                        errorList.add("Quantity requested for '" + supply.getItemName() + "'  must be more than equals to " + minimum + " and less than equals to " + (quantitySupplied - minimum) + " OR equals to " + quantitySupplied);
                    } else if (quantityDemanded > maximum || quantityDemanded < minimum) {
                        errorList.add("Quantity requested for '" + supply.getItemName() + "'  must be more than equals to " + minimum + " and less than equals to " + maximum);
                    }

                    supplyList.add(supply);
                }

                //check if the errorlist is empty
                if (!errorList.isEmpty()) {
                    return new CreateDemandResponse(false, errorList);
                }

                //generate date requested
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                String dateRequested = sdf.format(today);

                //get supplier
                User supplier = supplyList.get(0).getUser();

                Demand newDemand = demandDAO.createDemand(new Demand(user, supplier, dateRequested, preferredDeliveryDateStr, preferredTimeslot, preferredSchedule, "Pending", ""));

                String requestContent = "";

                //create demanditems
                for (int i = 0; i < supplyIDValues.length; i++) {
                    Supply supply = supplyList.get(i);
                    int quantityDemanded = quantityDemandedIntValues[i];

                    demandDAO.createDemandItem(new DemandItem(newDemand, supply, quantityDemanded));
                    requestContent += "<tr><td style='text-align:center;'>" + supply.getItemName() + "</td><td style='text-align:center;'>" + quantityDemanded + "</td></tr>";
                }

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
                    String recipient = supplier.getEmail();

                    String body = "<div>\n"
                            + "            <p>Dear " + supplier.getOrganizationName() + ",</p><br/>\n"
                            + "            <p>You have a new request from <b>" + user.getOrganizationName() + "</b>. Here are the request details:</p>\n"
                            + "            <table border='1'>"
                            + "                <tr>"
                            + "                    <th>Item Name</th>"
                            + "                    <th>Quantity Requested</th>"
                            + "                </tr>"
                            + requestContent
                            + "            </table>"
                            + "            <p>Please login <a href='http://apps.greentransformationlab.com/Wheels4Food/Login'>here</a> to approve this request!</p><br/>\n"
                            + "            <p>Regards,</p>\n"
                            + "            <p>Wheels4Food Team</p>\n"
                            + "        </div>";
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(emailUsername));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                    message.setSubject("Wheels4Food - New request from " + user.getOrganizationName());
                    message.setText(body, "UTF-8", "html");

                    Transport.send(message);
                } catch (MessagingException e) {
                    errorList.add(e.getMessage());
                    return new CreateDemandResponse(false, errorList);
                }

                //create notification
                notificationDAO.createNotification(new Notification(supplier, "PendingApprovals", "<b>" + user.getOrganizationName() + "</b> has requested for <b>" + supplyIDValues.length + " item(s)</b>. Click here to go to <b>Pending Approvals</b>."));

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

                List<DemandItem> demandItemList = demandDAO.getDemandItemListByDemandId(demand.getId());

                //delete all the demanditems under this demand first
                for (DemandItem demandItem : demandItemList) {
                    demandDAO.deleteDemandItem(demandItem.getId());
                }

                //finally delete the demand itself
                demandDAO.deleteDemand(id);

                //create notification
                notificationDAO.createNotification(new Notification(demand.getSupplier(), "PendingApprovals", "<b>" + demand.getUser().getOrganizationName() + "</b> has <b>cancelled</b> the request for <b>" + demandItemList.size() + " item(s)</b>. Click here to go to <b>Pending Approvals</b>."));

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

    public UpdateDemandResponse updateDemandRequest(UpdateDemandRequest request) {
        Demand demand = request.getDemand();
        List<DemandItem> demandItemList = request.getDemandItemList();
        String preferredDeliveryDateStr = demand.getPreferredDeliveryDate();
        String preferredTimeslot = demand.getPreferredTimeslot();
        String preferredSchedule = demand.getPreferredSchedule();

        ArrayList<String> errorList = new ArrayList<String>();

        for (DemandItem demandItem : demandItemList) {
            int quantityDemanded = demandItem.getQuantityDemanded();

            if (quantityDemanded <= 0) {
                errorList.add("All Quantity Requested fields must be an integer that is more than 0.");
                break;
            }
        }

        if (preferredDeliveryDateStr.equals("")) {
            errorList.add("Preferred Delivery Date cannot be blank.");
        }

        if (preferredTimeslot.equals("")) {
            errorList.add("Preferred Timeslot requested cannot be blank.");
        }

        if (preferredSchedule.equals("")) {
            errorList.add("Preferred Schedule cannot be blank.");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new UpdateDemandResponse(false, errorList);
        }

        if (preferredSchedule.equals("NA")) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date preferredDeliveryDate = sdf.parse(preferredDeliveryDateStr);

                if (today.compareTo(preferredDeliveryDate) >= 0 || (preferredDeliveryDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24) <= 3) {
                    errorList.add("Preferred Delivery Date must be a date at least 3 days after today");
                    return new UpdateDemandResponse(false, errorList);
                }
            } catch (ParseException e) {
                errorList.add("Invalid Preferred Delivery Date");
            }
        } else {
            if (StringUtils.countOccurrencesOf(preferredSchedule, "1") < 3) {
                errorList.add("A minimum of 3 timeslots must be selected.");
            }
        }

        try {
            for (DemandItem demandItem : demandItemList) {
                Supply supply = demandItem.getSupply();

                //get min and max of supply
                int minimum = supply.getMinimum();
                int maximum = supply.getMaximum();

                int quantitySupplied = supply.getQuantitySupplied();

                int quantityDemanded = demandItem.getQuantityDemanded();

                //check if quantity demanded is in the valid range
                if ((quantitySupplied - minimum < minimum) && quantityDemanded != quantitySupplied) {
                    errorList.add("Quantity requested for '" + supply.getItemName() + "' must be equals to " + quantitySupplied);
                } else if (quantitySupplied - minimum == minimum && quantityDemanded != quantitySupplied && quantityDemanded != minimum) {
                    errorList.add("Quantity requested for '" + supply.getItemName() + "'  must be equals to " + minimum + " OR equals to " + maximum);
                } else if ((minimum + maximum) > quantitySupplied && (quantitySupplied - quantityDemanded) < minimum && (quantitySupplied - quantityDemanded) != 0) {
                    errorList.add("Quantity requested for '" + supply.getItemName() + "'  must be more than equals to " + minimum + " and less than equals to " + (quantitySupplied - minimum) + " OR equals to " + quantitySupplied);
                } else if (quantityDemanded > maximum || quantityDemanded < minimum) {
                    errorList.add("Quantity requested for '" + supply.getItemName() + "'  must be more than equals to " + minimum + " and less than equals to " + maximum);
                }
            }

            //check if the errorlist is empty
            if (!errorList.isEmpty()) {
                return new UpdateDemandResponse(false, errorList);
            }

            //get the current list of demanditems
            List<DemandItem> currentDemandItemList = demandDAO.getDemandItemListByDemandId(demand.getId());
            ArrayList<Integer> currentDemandItemIDs = new ArrayList<Integer>();

            for (DemandItem demandItem : currentDemandItemList) {
                currentDemandItemIDs.add(demandItem.getId());
            }
            System.out.println(currentDemandItemIDs.size());
            //update the demanditems first
            for (DemandItem demandItem : demandItemList) {
                //check whether demanditem has been removed
                if (currentDemandItemIDs.contains(demandItem.getId())) {
                    demandDAO.updateDemandItem(demandItem);
                    currentDemandItemIDs.remove(new Integer(demandItem.getId()));
                }
            }

            for (int id : currentDemandItemIDs) {
                demandDAO.deleteDemandItem(id);
            }

            demandDAO.updateDemand(demand);
            return new UpdateDemandResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new UpdateDemandResponse(false, errorList);
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

            String requestContent = "";

            try {
                List<DemandItem> demandItemList = demandDAO.getDemandItemListByDemandId(id);

                for (DemandItem demandItem : demandItemList) {
                    Supply supply = demandItem.getSupply();
                    int quantitySupplied = supply.getQuantitySupplied();
                    int quantityDemanded = demandItem.getQuantityDemanded();

                    supply.setQuantitySupplied(quantitySupplied - quantityDemanded);

                    if (supply.getMaximum() < supply.getQuantitySupplied()) {
                        supply.setMaximum(supply.getQuantitySupplied());
                    }

                    try {
                        supplyDAO.updateSupply(supply);
                        requestContent += "<tr><td style='text-align:center;'>" + supply.getItemName() + "</td><td style='text-align:center;'>" + quantityDemanded + "</td></tr>";
                    } catch (Exception e) {
                        errorList.add(e.getMessage());
                        return new ApproveDemandResponse(false, errorList);
                    }
                }

                demandDAO.updateDemand(demand);

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
                    return new ApproveDemandResponse(false, errorList);
                }

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

            if (!status.equals("Pending")) {
                errorList.add("Status of request must be pending");
                return new RejectDemandResponse(false, errorList);
            }

            demand.setComments(comments);
            demand.setStatus("Rejected");

            try {
                demandDAO.updateDemand(demand);

                //create notification
                notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "Your requested item(s) have been <b>rejected</b> by <b>" + demand.getSupplier().getOrganizationName() + ".</b> Click here to go to <b>My Inventory - Demand</b>."));

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

    public List<Demand> getCompletedDemandListBySupplierIdRequest(int supplierID) throws Exception {
        return demandDAO.getCompletedDemandListBySupplierId(supplierID);
    }

    public List<Demand> getApprovedDemandListBySupplierIdRequest(int supplierID) throws Exception {
        return demandDAO.getApprovedDemandListBySupplierId(supplierID);
    }

    public List<DemandItem> getDemandItemListRequest() throws Exception {
        return demandDAO.getDemandItemList();
    }

    public List<DemandItem> getDemandItemListByDemandIdRequest(int demandID) throws Exception {
        return demandDAO.getDemandItemListByDemandId(demandID);
    }

    public List<DemandItem> getDemandItemListByRequesterIdRequest(int requesterID) throws Exception {
        return demandDAO.getDemandItemListByRequesterId(requesterID);
    }

    public List<DemandItem> getDemandItemListBySupplierIdRequest(int supplierID) throws Exception {
        return demandDAO.getDemandItemListBySupplierId(supplierID);
    }

    public List<String> getUnavailableTimeslotsByDeliveryDateRequest(GetUnavailableTimeslotsByDeliveryDateRequest request) throws Exception {
        List<Demand> demandList = demandDAO.getDemandListByDeliveryDate(request.getSupplierID(), request.getDeliveryDate());

        List<String> unavailableTimeslots = new ArrayList<String>();

        for (Demand d : demandList) {
            unavailableTimeslots.add(d.getPreferredTimeslot());
        }

        return unavailableTimeslots;
    }

    public Demand getDemandByIdRequest(int id) throws Exception {
        return demandDAO.getDemandById(id);
    }

    public GetDemandBreakdownResponse getDemandBreakdownRequest(int id) throws Exception {
        List<Demand> demandList = demandDAO.getDemandListBySupplierOrRequesterId(id);

        int pending = 0;
        int approved = 0;
        int rejected = 0;

        for (Demand demand : demandList) {
            String status = demand.getStatus();

            switch (status) {
                case "Pending":
                    pending++;
                    break;
                case "Rejected":
                    rejected++;
                    break;
                default:
                    approved++;
                    break;
            }
        }

        return new GetDemandBreakdownResponse(pending, approved, rejected);
    }
}
