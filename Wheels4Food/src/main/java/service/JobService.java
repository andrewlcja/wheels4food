/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.JobDAO;
import dao.SupplyDAO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import model.CreateJobRequest;
import model.CreateJobResponse;
import model.CreateSupplyResponse;
import model.Demand;
import model.Job;
import model.Supply;
import model.UpdateJobResponse;
import org.springframework.beans.factory.annotation.Autowired;

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

    public CreateJobResponse createJobRequest(CreateJobRequest request) {
        int demandID = request.getDemandID();
        boolean monday = request.isMonday();
        boolean tuesday = request.isTuesday();
        boolean wednesday = request.isWednesday();
        boolean thursday = request.isThursday();
        boolean friday = request.isFriday();
        String comments = request.getComments();

        Demand demand = demandDAO.getDemandById(demandID);
        String status = demand.getStatus();

        ArrayList<String> errorList = new ArrayList<String>();

        if (!status.equals("Pending")) {
            errorList.add("Status of request must be pending");
            return new CreateJobResponse(false, errorList);
        }
        
        int count = 0;
        if (monday) {
            count++;
        }

        if (tuesday) {
            count++;
        }

        if (wednesday) {
            count++;
        }

        if (thursday) {
            count++;
        }

        if (friday) {
            count++;
        }

        if (count < 2) {
            errorList.add("At least 3 days must be selected");
            return new CreateJobResponse(false, errorList);
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
                return new CreateJobResponse(false, errorList);
            }

            demand.setQuantityDemanded(quantitySupplied);
            demand.setComments(comments);
            supply.setQuantitySupplied(0);
        }

        try {
            supplyDAO.updateSupply(supply);
            demandDAO.updateDemand(demand);

            //generate expiry date (2 weeks)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            Date expiryDate = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
            String expiryDateStr = sdf.format(expiryDate);

            jobDAO.createJob(new Job(demand, monday, tuesday, wednesday, thursday, friday, expiryDateStr));
            return new CreateJobResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreateJobResponse(false, errorList);
        }
    }

    public UpdateJobResponse updateJobRequest(Job job) {
        boolean monday = job.isMonday();
        boolean tuesday = job.isTuesday();
        boolean wednesday = job.isWednesday();
        boolean thursday = job.isThursday();
        boolean friday = job.isFriday();

        ArrayList<String> errorList = new ArrayList<String>();

        int count = 0;
        if (monday) {
            count++;
        }

        if (tuesday) {
            count++;
        }

        if (wednesday) {
            count++;
        }

        if (thursday) {
            count++;
        }

        if (friday) {
            count++;
        }

        if (count < 2) {
            errorList.add("At least 2 days must be selected");
            return new UpdateJobResponse(false, errorList);
        }

        try {
            Demand demand = job.getDemand();
            demand.setStatus("Job Created");

            demandDAO.updateDemand(demand);
            jobDAO.updateJob(job);
            
            return new UpdateJobResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new UpdateJobResponse(false, errorList);
        }
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
}
