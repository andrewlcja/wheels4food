/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.JobDAO;
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
import model.AcceptJobRequest;
import model.AcceptJobResponse;
import model.CancelJobByDemandIdResponse;
import model.CompleteJobByDemandIdResponse;
import model.CreateJobRequest;
import model.CreateJobResponse;
import model.CreateSelfCollectionResponse;
import model.Demand;
import model.DemandItem;
import model.GetJobBreakdownBySupplierIdResponse;
import model.GetJobBreakdownBySupplierIdAndDateRequest;
import model.Job;
import model.Notification;
import model.Supply;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import utility.ConfigUtility;

/**
 *
 * @author andrew.lim.2013
 */
public class JobService {

    @Autowired
    JobDAO jobDAO;

    @Autowired
    DemandDAO demandDAO;

    @Autowired
    SupplyDAO supplyDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    NotificationDAO notificationDAO;

    public CreateJobResponse createJobRequest(CreateJobRequest request) {
        int demandID = request.getDemandID();
        int userID = request.getUserID();
        String deliveryDateStr = request.getDeliveryDate().trim();
        String timeslot = request.getTimeslot().trim();

        Demand demand = demandDAO.getDemandById(demandID);

        String status = demand.getStatus();

        ArrayList<String> errorList = new ArrayList<String>();

        if (!status.equals("Pending")) {
            errorList.add("Status of request must be pending.");
            return new CreateJobResponse(false, errorList);
        }

        if (deliveryDateStr.equals("")) {
            errorList.add("Delivery Date cannot be blank.");
        }

        if (timeslot.equals("")) {
            errorList.add("Timeslot cannot be blank.");
        }

        if (!errorList.isEmpty()) {
            return new CreateJobResponse(false, errorList);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));;

        try {
            Date deliveryDate = sdf.parse(deliveryDateStr);
        } catch (ParseException e) {
            errorList.add("Invalid Delivery Date");
            return new CreateJobResponse(false, errorList);
        }

        demand.setStatus("Job Created");

        String requestContent = "";
        try {
            List<DemandItem> demandItemList = demandDAO.getDemandItemListByDemandId(demand.getId());

            for (DemandItem demandItem : demandItemList) {
                Supply supply = demandItem.getSupply();
                int quantitySupplied = supply.getQuantitySupplied();
                int quantityDemanded = demandItem.getQuantityDemanded();

                supply.setQuantitySupplied(quantitySupplied - quantityDemanded);

                if (supply.getMaximum() > supply.getQuantitySupplied()) {
                    supply.setMaximum(supply.getQuantitySupplied());
                }

                supplyDAO.updateSupply(supply);
                requestContent += "<tr><td style='text-align:center;'>" + supply.getItemName() + "</td><td style='text-align:center;'>" + quantityDemanded + "</td></tr>";
            }

            demandDAO.updateDemand(demand);

            //generate expiry date (2 weeks)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            Date expiryDate = calendar.getTime();
            String expiryDateStr = sdf.format(expiryDate);

            User user = userDAO.getUserById(userID);
            if (user == null) {
                errorList.add("Invalid user id");
            }

            if (!errorList.isEmpty()) {
                return new CreateJobResponse(false, errorList);
            }

            jobDAO.createJob(new Job(demand, user, deliveryDateStr, timeslot, expiryDateStr, "Active", "", ""));

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
                return new CreateJobResponse(false, errorList);
            }

            //create notification
            notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "Your requested item(s) have been <b>approved</b> by <b>" + demand.getSupplier().getOrganizationName() + ".</b> Click here to go to <b>My Inventory - Demand</b>."));

            return new CreateJobResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreateJobResponse(false, errorList);
        }
    }

    public AcceptJobResponse acceptJobRequest(AcceptJobRequest request) {
        int jobID = request.getJobID();
        int userID = request.getUserID();
        String collectionTime = request.getCollectionTime();
        String deliveryTime = request.getDeliveryTime();

        ArrayList<String> errorList = new ArrayList<String>();

        if (jobID <= 0) {
            errorList.add("Invalid job id");
        }

        if (userID <= 0) {
            errorList.add("Invalid user id");
        }

        if (collectionTime.equals("")) {
            errorList.add("Collection Time cannot be blank.");
        }

        if (deliveryTime.equals("")) {
            errorList.add("Delivery Time cannot be blank.");
        }

        if (!errorList.isEmpty()) {
            return new AcceptJobResponse(false, errorList);
        }

        if (!errorList.isEmpty()) {
            return new AcceptJobResponse(false, errorList);
        }

        Job job = jobDAO.getJobById(jobID);

        try {
            User user = userDAO.getUserById(userID);

            if (user == null) {
                errorList.add("Invalid user id.");
                return new AcceptJobResponse(false, errorList);
            }

            job.setUser(user);
            job.setCollectionTime(collectionTime);
            job.setDeliveryTime(deliveryTime);
            job.setStatus("Accepted");

            jobDAO.updateJob(job);

            Demand demand = job.getDemand();
            demand.setStatus("Job Accepted");
            demandDAO.updateDemand(demand);

            //create notification
            notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "Volunteer, <b>" + job.getUser().getPocName() + "</b> has <b>accepted</b> your job. Click here to go to <b>My Inventory - Demand</b>."));
            notificationDAO.createNotification(new Notification(demand.getSupplier(), "ApprovedRequests", "Volunteer, <b>" + job.getUser().getPocName() + "</b> has <b>accepted</b> your job. Click here to go to <b>Approved Requests</b>."));

            return new AcceptJobResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new AcceptJobResponse(false, errorList);
        }

    }

    public CancelJobByDemandIdResponse cancelJobByDemandIdRequest(Demand demand) {
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
            return new CancelJobByDemandIdResponse(false, errorList);
        }

        Job job = jobDAO.getJobByDemandId(demandID);
        job.setStatus("Cancelled");

        demand.setStatus("Job Cancelled");

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
                notificationDAO.createNotification(new Notification(demand.getSupplier(), "ApprovedRequests", "A job has been <b>cancelled</b> due to Requesting Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>Approved Requests</b>."));
                notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "A job has been <b>cancelled</b> due to Requesting Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>My Inventory - Demand</b>."));

                if (demand.getStatus().equals("Job Accepted")) {
                    notificationDAO.createNotification(new Notification(job.getUser(), "MyJobs", "A job has been <b>cancelled</b> due to Requesting Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>My Jobs</b>."));
                }
            } else if (comments.contains("Supplying")) {
                deductUser = demandItemList.get(0).getSupply().getUser();

                //create notification
                notificationDAO.createNotification(new Notification(demand.getSupplier(), "ApprovedRequests", "A job has been <b>cancelled</b> due to Supplying Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>Approved Requests</b>."));
                notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "A job has been <b>cancelled</b> due to Supplying Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>My Inventory - Demand</b>."));

                if (demand.getStatus().equals("Job Accepted")) {
                    notificationDAO.createNotification(new Notification(job.getUser(), "MyJobs", "A job has been <b>cancelled</b> due to Supplying Organization, <b>" + deductUser.getOrganizationName() + "</b>. Click here to go to <b>My Jobs</b>."));
                }
            } else if (comments.contains("Volunteer")) {
                deductUser = job.getUser();

                //create notification
                notificationDAO.createNotification(new Notification(demand.getSupplier(), "ApprovedRequests", "A job has been <b>cancelled</b> due to Volunteer, <b>" + deductUser.getPocName() + "</b>. Click here to go to <b>Approved Requests</b>."));
                notificationDAO.createNotification(new Notification(demand.getUser(), "Inventory.Demand", "A job has been <b>cancelled</b> due to Volunteer, <b>" + deductUser.getPocName() + "</b>. Click here to go to <b>My Inventory - Demand</b>."));
                notificationDAO.createNotification(new Notification(job.getUser(), "MyJobs", "A job has been <b>cancelled</b> due to Volunteer, <b>" + deductUser.getPocName() + "</b>. Click here to go to <b>My Jobs</b>."));
            }

            if (deductUser != null) {
                deductUser.setDemeritPoints(deductUser.getDemeritPoints() + 1);
            }

            jobDAO.updateJob(job);
            demandDAO.updateDemand(demand);
            userDAO.updateUser(deductUser);
            return new CancelJobByDemandIdResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CancelJobByDemandIdResponse(false, errorList);
        }
    }

    public CompleteJobByDemandIdResponse completeJobByDemandIdRequest(int demandID) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (demandID <= 0) {
            errorList.add("Invalid demand id");
            return new CompleteJobByDemandIdResponse(false, errorList);
        }

        Job job = jobDAO.getJobByDemandId(demandID);
        job.setStatus("Completed");

        Demand demand = demandDAO.getDemandById(demandID);
        demand.setStatus("Job Completed");

        try {
            jobDAO.updateJob(job);
            demandDAO.updateDemand(demand);

            //create notification
            notificationDAO.createNotification(new Notification(job.getUser(), "MyJobs", "<b>" + demand.getUser().getOrganizationName() + "</b> has <b>completed</b> a job. Click here to go to <b>My Jobs</b>."));
            notificationDAO.createNotification(new Notification(demand.getSupplier(), "ApprovedRequests", "<b>" + demand.getUser().getOrganizationName() + "</b> has <b>completed</b> a job. Click here to go to <b>Approved Requests</b>."));

            return new CompleteJobByDemandIdResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CompleteJobByDemandIdResponse(false, errorList);
        }
    }

    public GetJobBreakdownBySupplierIdResponse getJobBreakdownBySupplierIdRequest(int supplierID) throws Exception {
        List<Job> jobList = jobDAO.getJobListBySupplierId(supplierID);

        int pending = 0;
        int accepted = 0;
        int cancelled = 0;
        int completed = 0;

        for (Job job : jobList) {
            String status = job.getDemand().getStatus();

            switch (status) {
                case "Job Created":
                    pending++;
                    break;
                case "Job Accepted":
                    accepted++;
                    break;
                case "Job Cancelled":
                    cancelled++;
                    break;
                case "Job Completed":
                    completed++;
                    break;
            }
        }

        return new GetJobBreakdownBySupplierIdResponse(pending, accepted, cancelled, completed);
    }
    
    public GetJobBreakdownBySupplierIdResponse getJobBreakdownBySupplierIdAndDateRequest(GetJobBreakdownBySupplierIdAndDateRequest request) throws Exception {
        List<Job> jobList = jobDAO.getJobListBySupplierId(request.getId());

        int pending = 0;
        int accepted = 0;
        int cancelled = 0;
        int completed = 0;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        for (Job job : jobList) {
            String status = job.getDemand().getStatus();
            
            Date dateRequested = sdf.parse(job.getDemand().getDateRequested());

            Calendar cal = Calendar.getInstance();
            cal.setTime(dateRequested);
            int currentMonth = cal.get(Calendar.MONTH) + 1;
            int currentYear = cal.get(Calendar.YEAR);

            if (currentMonth >= request.getStartMonth() && currentMonth <= request.getEndMonth() && currentYear == request.getYear()) {
                switch (status) {
                    case "Job Created":
                        pending++;
                        break;
                    case "Job Accepted":
                        accepted++;
                        break;
                    case "Job Cancelled":
                        cancelled++;
                        break;
                    case "Job Completed":
                        completed++;
                        break;
                }
            }
        }

        return new GetJobBreakdownBySupplierIdResponse(pending, accepted, cancelled, completed);
    }

    public List<Job> getJobListRequest() throws Exception {
        return jobDAO.retrieveAll();
    }

    public Job getJobByDemandIdRequest(int demandID) throws Exception {
        return jobDAO.getJobByDemandId(demandID);
    }

    public Job getJobByIdRequest(int id) throws Exception {
        return jobDAO.getJobById(id);
    }

    public List<Job> getJobListByUserIdRequest(int userID) throws Exception {
        return jobDAO.getJobListByUserId(userID);
    }

    public List<Job> getJobListByOrganizationNameRequest(String organizationName) throws Exception {
        return jobDAO.getJobListByOrganizationName(organizationName);
    }
}
