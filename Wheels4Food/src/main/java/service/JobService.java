/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.JobDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import model.AcceptJobRequest;
import model.AcceptJobResponse;
import model.CancelJobByDemandIdResponse;
import model.CompleteJobByDemandIdResponse;
import model.ConfirmJobResponse;
import model.CreateJobRequest;
import model.CreateJobResponse;
import model.Demand;
import model.DemandItem;
import model.GetJobBreakdownBySupplierIdResponse;
import model.Job;
import model.Supply;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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

    public CreateJobResponse createJobRequest(CreateJobRequest request) {
        int demandID = request.getDemandID();
        String schedule = request.getSchedule();
        String comments = request.getComments();
        int userID = request.getUserID();

        Demand demand = demandDAO.getDemandById(demandID);

        String status = demand.getStatus();

        ArrayList<String> errorList = new ArrayList<String>();

        if (!status.equals("Pending")) {
            errorList.add("Status of request must be pending.");
            return new CreateJobResponse(false, errorList);
        }

        if (StringUtils.countOccurrencesOf(schedule, "1") < 3) {
            errorList.add("A minimum of 3 timeslots must be selected.");
            return new CreateJobResponse(false, errorList);
        }

        demand.setStatus("Job Created");

        try {
            List<DemandItem> demandItemList = demandDAO.getDemandItemListByDemandId(demand.getId());

            for (DemandItem demandItem : demandItemList) {
                Supply supply = demandItem.getSupply();
                int quantitySupplied = supply.getQuantitySupplied();
                int quantityDemanded = demandItem.getQuantityDemanded();

                supply.setQuantitySupplied(quantitySupplied - quantityDemanded);

                if (supply.getMaximum() < supply.getQuantitySupplied()) {
                    supply.setMaximum(supply.getQuantitySupplied());
                }

                supplyDAO.updateSupply(supply);
            }

            demandDAO.updateDemand(demand);

            //generate expiry date (2 weeks)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            Date expiryDate = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
            String expiryDateStr = sdf.format(expiryDate);

            User user = userDAO.getUserById(userID);
            if (user == null) {
                errorList.add("Invalid user id");
            }

            if (!errorList.isEmpty()) {
                return new CreateJobResponse(false, errorList);
            }

            jobDAO.createJob(new Job(demand, schedule, expiryDateStr, "Active", user, "", "", ""));
            return new CreateJobResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreateJobResponse(false, errorList);
        }
    }

    public ConfirmJobResponse confirmJobRequest(Job job) {
        String schedule = job.getSchedule();

        ArrayList<String> errorList = new ArrayList<String>();

        if (StringUtils.countOccurrencesOf(schedule, "1") < 3) {
            errorList.add("A minimum of 3 timeslots must be selected.");
            return new ConfirmJobResponse(false, errorList);
        }

        try {
            Demand demand = job.getDemand();
            demand.setStatus("Job Created");

            demandDAO.updateDemand(demand);

            job.setStatus("Active");
            jobDAO.updateJob(job);

            return new ConfirmJobResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new ConfirmJobResponse(false, errorList);
        }
    }

    public AcceptJobResponse acceptJobRequest(AcceptJobRequest request) {
        int jobID = request.getJobID();
        int userID = request.getUserID();
        String deliveryDateStr = request.getDeliveryDate();
        String collectionTime = request.getCollectionTime();
        String deliveryTime = request.getDeliveryTime();

        ArrayList<String> errorList = new ArrayList<String>();

        if (jobID <= 0) {
            errorList.add("Invalid job id");
        }

        if (userID <= 0) {
            errorList.add("Invalid user id");
        }

        if (deliveryDateStr.equals("")) {
            errorList.add("Delivery Date cannot be blank.");
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

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        try {
            Date deliveryDate = sdf.parse(deliveryDateStr);
        } catch (ParseException e) {
            errorList.add("Invalid Expiry Date");
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
            job.setDeliveryDate(deliveryDateStr);
            job.setCollectionTime(collectionTime);
            job.setDeliveryTime(deliveryTime);
            job.setStatus("Accepted");

            jobDAO.updateJob(job);

            Demand demand = job.getDemand();
            demand.setStatus("Job Accepted");
            demandDAO.updateDemand(demand);

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
            } else if (comments.contains("Supplying")) {
                deductUser = demandItemList.get(0).getSupply().getUser();
            } else if (comments.contains("Volunteer")) {
                deductUser = job.getUser();
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
}
