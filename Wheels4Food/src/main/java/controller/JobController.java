/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.CreateJobRequest;
import model.CreateJobResponse;
import model.Job;
import model.UpdateJobResponse;
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
    
    @RequestMapping(value = "/UpdateJobRequest", method = RequestMethod.PUT)
    public @ResponseBody
    UpdateJobResponse updateJobRequest(@RequestBody Job job) {
        return jobService.updateJobRequest(job);
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
    
    @RequestMapping(value = "/GetJobByDemandIdRequest/{demandID}", method = RequestMethod.GET)
    public @ResponseBody Job getJobByDemandIdRequest(@PathVariable("demandID") int demandID) {
        Job job = null;
        
        try {
            job = jobService.getJobByDemandIdRequest(demandID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return job;
    }
}
