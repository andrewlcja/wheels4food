/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.ApproveDemandRequest;
import model.ApproveDemandResponse;
import model.CreateDemandRequest;
import model.CreateDemandResponse;
import model.DeleteDemandResponse;
import model.Demand;
import model.RejectDemandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.DemandService;

/**
 *
 * @author andrew.lim.2013
 */
@Controller
public class DemandController {

    @Autowired
    DemandService demandService;

    @RequestMapping(value = "/CreateDemandRequest", method = RequestMethod.POST)
    public @ResponseBody
    CreateDemandResponse createDemandRequest(@RequestBody CreateDemandRequest request) {
        return demandService.createDemandRequest(request);
    }
    
    @RequestMapping(value = "/DeleteDemandRequest/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    DeleteDemandResponse deleteDemandRequest(@PathVariable("id") String id) {
        return demandService.deleteDemandRequest(id);
    }
    
    @RequestMapping(value = "/GetDemandListByUserIdRequest/{userID}", method = RequestMethod.GET)
    public @ResponseBody
    List<Demand> getDemandListByUserIdRequest(@PathVariable("userID") int userID) {
        List<Demand> demandList = null;

        try {
            demandList = demandService.getDemandListByUserIdRequest(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return demandList;
    }
    
    @RequestMapping(value = "/GetPendingDemandListBySupplierIdRequest/{supplierID}", method = RequestMethod.GET)
    public @ResponseBody
    List<Demand> getPendingDemandListBySupplyIdRequest(@PathVariable("supplierID") int supplierID) {
        List<Demand> demandList = null;

        try {
            demandList = demandService.getPendingDemandListBySupplierIdRequest(supplierID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return demandList;
    }
    
    @RequestMapping(value = "/ApproveDemandRequest/{id}", method = RequestMethod.PUT)
    public @ResponseBody ApproveDemandResponse approveDemandRequest(@PathVariable("id") String id, @RequestBody ApproveDemandRequest request) {
        return demandService.approveDemandRequest(id, request);
    }
    
    @RequestMapping(value = "/RejectDemandRequest/{id}", method = RequestMethod.PUT)
    public @ResponseBody RejectDemandResponse rejectDemandRequest(@PathVariable("id") String id) {
        return demandService.rejectDemandRequest(id);
    }
}
