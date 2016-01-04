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
import model.ConfirmJobResponse;
import model.CreateJobRequest;
import model.CreateJobResponse;
import model.Demand;
import model.Job;
import model.Supply;
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

    public CreateJobResponse createJobRequest(CreateJobRequest request) {
        int demandID = request.getDemandID();
        String schedule = request.getSchedule();
        String comments = request.getComments();

        Demand demand = demandDAO.getDemandById(demandID);
        String status = demand.getStatus();

        ArrayList<String> errorList = new ArrayList<String>();

        if (!status.equals("Pending")) {
            errorList.add("Status of request must be pending.");
            return new CreateJobResponse(false, errorList);
        }

        if (StringUtils.countOccurrencesOf(schedule, "1") < 5) {
            errorList.add("A minimum of 5 timeslots must be selected.");
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

            jobDAO.createJob(new Job(demand, schedule, expiryDateStr, "Inactive"));
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
