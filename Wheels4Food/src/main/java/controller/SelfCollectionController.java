/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.CancelSelfCollectionByDemandIdResponse;
import model.CompleteSelfCollectionByDemandIdResponse;
import model.CreateSelfCollectionRequest;
import model.CreateSelfCollectionResponse;
import model.Demand;
import model.GetSelfCollectionBreakdownBySupplierIdResponse;
import model.GetSelfCollectionBreakdownBySupplierIdAndDateRequest;
import model.SelfCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.SelfCollectionService;

/**
 *
 * @author andrew.lim.2013
 */
@Controller
public class SelfCollectionController {

    @Autowired
    SelfCollectionService selfCollectionService;

    @RequestMapping(value = "/CreateSelfCollectionRequest", method = RequestMethod.POST)
    public @ResponseBody
    CreateSelfCollectionResponse createSelfCollectionRequest(@RequestBody CreateSelfCollectionRequest request) {
        return selfCollectionService.createSelfCollectionRequest(request);
    }

    @RequestMapping(value = "/GetSelfCollectionByDemandIdRequest/{demandID}", method = RequestMethod.GET)
    public @ResponseBody
    SelfCollection getSelfCollectionByDemandIdRequest(@PathVariable("demandID") int demandID) {
        SelfCollection selfCollection = null;

        try {
            selfCollection = selfCollectionService.getSelfCollectionByDemandIdRequest(demandID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return selfCollection;
    }
    
    @RequestMapping(value = "/GetSelfCollectionBreakdownBySupplierIdRequest/{supplierID}", method = RequestMethod.GET)
    public @ResponseBody
    GetSelfCollectionBreakdownBySupplierIdResponse getSelfCollectionBreakdownBySupplierIdRequest(@PathVariable("supplierID") int supplierID) {
        GetSelfCollectionBreakdownBySupplierIdResponse response = null;
        
        try {
            response = selfCollectionService.getSelfCollectionBreakdownBySupplierIdRequest(supplierID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return response;
    }
    
    @RequestMapping(value = "/GetSelfCollectionBreakdownBySupplierIdAndDateRequest", method = RequestMethod.POST)
    public @ResponseBody
    GetSelfCollectionBreakdownBySupplierIdResponse getSelfCollectionBreakdownBySupplierIdAndDateRequest(@RequestBody GetSelfCollectionBreakdownBySupplierIdAndDateRequest request) {
        GetSelfCollectionBreakdownBySupplierIdResponse response = null;
        
        try {
            response = selfCollectionService.getSelfCollectionBreakdownBySupplierIdAndDateRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return response;
    }
    
    @RequestMapping(value = "/CancelSelfCollectionByDemandIdRequest", method = RequestMethod.PUT)
    public @ResponseBody
    CancelSelfCollectionByDemandIdResponse cancelSelfCollectionByDemandIdRequest(@RequestBody Demand demand) {
        return selfCollectionService.cancelSelfCollectionByDemandIdRequest(demand);
    }
    
    @RequestMapping(value = "/CompleteSelfCollectionByDemandIdRequest/{demandID}", method = RequestMethod.PUT)
    public @ResponseBody
    CompleteSelfCollectionByDemandIdResponse completeSelfCollectionByDemandIdRequest(@PathVariable("demandID") int demandID) {
        return selfCollectionService.completeSelfCollectionByDemandIdRequest(demandID);
    }
}
