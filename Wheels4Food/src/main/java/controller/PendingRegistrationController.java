/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.ApprovePendingRegistrationResponse;
import model.CreatePendingRegistrationRequest;
import model.CreatePendingRegistrationResponse;
import model.DeletePendingRegistrationResponse;
import model.PendingRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.PendingRegistrationService;

/**
 *
 * @author andrew.lim.2013
 */
@Controller
public class PendingRegistrationController {
    @Autowired
    PendingRegistrationService pendingRegistrationService;
    
    @RequestMapping(value = "/GetPendingRegistrationListRequest", method = RequestMethod.GET)
    public @ResponseBody List<PendingRegistration> getPendingRegistrationListRequest() {

        List<PendingRegistration> pendingRegistrationList = null;
        try {
            pendingRegistrationList = pendingRegistrationService.getPendingRegistrationListRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pendingRegistrationList;
    }
    
    @RequestMapping(value = "/CreatePendingRegistrationRequest", method = RequestMethod.POST)
    public @ResponseBody CreatePendingRegistrationResponse createPendingRegistrationRequest(@RequestBody CreatePendingRegistrationRequest request) {
        return pendingRegistrationService.createPendingRegistrationRequest(request);
    }
    
    @RequestMapping(value = "/DeletePendingRegistrationRequest/{id}", method = RequestMethod.DELETE)
    public @ResponseBody DeletePendingRegistrationResponse deletePendingRegistrationRequest(@PathVariable("id") String id) {
        return pendingRegistrationService.deletePendingRegistrationRequest(id);
    }
    
    @RequestMapping(value = "/GetPendingRegistrationByIdRequest/{id}", method = RequestMethod.GET)
    public @ResponseBody PendingRegistration getPendingRegistrationByIdRequest(@PathVariable("id") int id) {
        PendingRegistration pendingRegistration = null;
        
        try {
            pendingRegistration = pendingRegistrationService.getPendingRegistrationByIdRequest(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return pendingRegistration;
    }
    
    @RequestMapping(value = "/ApprovePendingRegistrationRequest/{id}", method = RequestMethod.PUT)
    public @ResponseBody ApprovePendingRegistrationResponse approvePendingRegistrationRequest(@PathVariable("id") String id) {
        return pendingRegistrationService.approvePendingRegistrationRequest(id);
    }
}
