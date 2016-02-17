/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.AcceptJobRequest;
import model.AcceptJobResponse;
import model.CancelJobByDemandIdResponse;
import model.CompleteJobByDemandIdResponse;
import model.CreateJobRequest;
import model.CreateJobResponse;
import model.Demand;
import model.GetJobBreakdownBySupplierIdResponse;
import model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.JobService;

/**
 *
 * @author andrew.lim.2013
 */
@Controller
public class JobController {

    @Autowired
    JobService jobService;

    @RequestMapping(value = "/CreateJobRequest", method = RequestMethod.POST)
    public @ResponseBody
    CreateJobResponse createJobRequest(@RequestBody CreateJobRequest request) {
        return jobService.createJobRequest(request);
    }

    @RequestMapping(value = "/AcceptJobRequest", method = RequestMethod.PUT)
    public @ResponseBody
    AcceptJobResponse confirmJobRequest(@RequestBody AcceptJobRequest request) {
        return jobService.acceptJobRequest(request);
    }
    
    @RequestMapping(value = "/CancelJobByDemandIdRequest", method = RequestMethod.PUT)
    public @ResponseBody
    CancelJobByDemandIdResponse cancelJobByDemandIdRequest(@RequestBody Demand demand) {
        return jobService.cancelJobByDemandIdRequest(demand);
    }

    @RequestMapping(value = "/GetJobListRequest", method = RequestMethod.GET)
    public @ResponseBody
    List<Job> getJobListRequest() {
        List<Job> jobList = null;

        try {
            jobList = jobService.getJobListRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobList;
    }

    @RequestMapping(value = "/GetJobListByUserIdRequest/{userID}", method = RequestMethod.GET)
    public @ResponseBody
    List<Job> getJobListByUserIdRequest(@PathVariable("userID") int userID) {
        List<Job> jobList = null;

        try {
            jobList = jobService.getJobListByUserIdRequest(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobList;
    }
    
    @RequestMapping(value = "/GetJobBreakdownBySupplierIdRequest/{supplierID}", method = RequestMethod.GET)
    public @ResponseBody
    GetJobBreakdownBySupplierIdResponse getJobBreakdownBySupplierIdRequest(@PathVariable("supplierID") int supplierID) {
        GetJobBreakdownBySupplierIdResponse response = null;
        
        try {
            response = jobService.getJobBreakdownBySupplierIdRequest(supplierID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return response;
    }

    @RequestMapping(value = "/GetJobByDemandIdRequest/{demandID}", method = RequestMethod.GET)
    public @ResponseBody
    Job getJobByDemandIdRequest(@PathVariable("demandID") int demandID) {
        Job job = null;

        try {
            job = jobService.getJobByDemandIdRequest(demandID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return job;
    }

    @RequestMapping(value = "/GetJobByIdRequest/{jobID}", method = RequestMethod.GET)
    public @ResponseBody
    Job getJobByIdRequest(@PathVariable("jobID") int jobID) {
        Job job = null;

        try {
            job = jobService.getJobByIdRequest(jobID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return job;
    }

    @RequestMapping(value = "/CompleteJobByDemandIdRequest/{demandID}", method = RequestMethod.PUT)
    public @ResponseBody
    CompleteJobByDemandIdResponse completeJobByDemandIdRequest(@PathVariable("demandID") int demandID) {
        return jobService.completeJobByDemandIdRequest(demandID);
    }
}
