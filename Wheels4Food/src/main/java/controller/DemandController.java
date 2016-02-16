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
import model.DemandItem;
import model.GetDemandBreakdownResponse;
import model.GetUnavailableTimeslotsByDeliveryDateRequest;
import model.RejectDemandRequest;
import model.RejectDemandResponse;
import model.UpdateDemandRequest;
import model.UpdateDemandResponse;
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
    
    @RequestMapping(value = "/GetDemandListBySupplyIdRequest/{supplyID}", method = RequestMethod.GET)
    public @ResponseBody
    List<Demand> getDemandListBySupplyIdRequest(@PathVariable("supplyID") int supplyID) {
        List<Demand> demandList = null;

        try {
            demandList = demandService.getDemandListBySupplyIdRequest(supplyID);
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
    
    @RequestMapping(value = "/GetCompletedDemandListBySupplierIdRequest/{supplierID}", method = RequestMethod.GET)
    public @ResponseBody
    List<Demand> getCompletedDemandListBySupplierIdRequest(@PathVariable("supplierID") int supplierID) {
        List<Demand> demandList = null;

        try {
            demandList = demandService.getCompletedDemandListBySupplierIdRequest(supplierID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return demandList;
    }
    
    @RequestMapping(value = "/GetDemandItemListByDemandIdRequest/{demandID}", method = RequestMethod.GET)
    public @ResponseBody
    List<DemandItem> getDemandItemListByDemandIdRequest(@PathVariable("demandID") int demandID) {
        List<DemandItem> demandItemList = null;

        try {
            demandItemList = demandService.getDemandItemListByDemandIdRequest(demandID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return demandItemList;
    }
    
    @RequestMapping(value = "/GetDemandItemListBySupplierIdRequest/{supplierID}", method = RequestMethod.GET)
    public @ResponseBody
    List<DemandItem> getDemandItemListBySupplierIdRequest(@PathVariable("supplierID") int supplierID) {
        List<DemandItem> demandItemList = null;

        try {
            demandItemList = demandService.getDemandItemListBySupplierIdRequest(supplierID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return demandItemList;
    }
    
    @RequestMapping(value = "/GetDemandItemListByRequesterIdRequest/{requesterID}", method = RequestMethod.GET)
    public @ResponseBody
    List<DemandItem> getDemandItemListByRequesterIdRequest(@PathVariable("requesterID") int requesterID) {
        List<DemandItem> demandItemList = null;

        try {
            demandItemList = demandService.getDemandItemListByRequesterIdRequest(requesterID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return demandItemList;
    }
    @RequestMapping(value = "/GetDemandItemListRequest", method = RequestMethod.GET)
    public @ResponseBody
    List<DemandItem> getDemandItemListRequest() {
        List<DemandItem> demandItemList = null;

        try {
            demandItemList = demandService.getDemandItemListRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return demandItemList;
    }
        
    @RequestMapping(value = "/GetUnavailableTimeslotsByDeliveryDateRequest", method = RequestMethod.POST)
    public @ResponseBody
    List<String> getUnavailableTimeslotsByDeliveryDateRequest(@RequestBody GetUnavailableTimeslotsByDeliveryDateRequest request) {
        List<String> unavailableTimeslots = null;

        try {
            unavailableTimeslots = demandService.getUnavailableTimeslotsByDeliveryDateRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return unavailableTimeslots;
    }
    
    @RequestMapping(value = "/UpdateDemandRequest", method = RequestMethod.PUT)
    public @ResponseBody
    UpdateDemandResponse updateDemandRequest(@RequestBody UpdateDemandRequest request) {
        return demandService.updateDemandRequest(request);
    }
    
    @RequestMapping(value = "/ApproveDemandRequest/{id}", method = RequestMethod.PUT)
    public @ResponseBody ApproveDemandResponse approveDemandRequest(@PathVariable("id") String id, @RequestBody ApproveDemandRequest request) {
        return demandService.approveDemandRequest(id, request);
    }
    
    @RequestMapping(value = "/RejectDemandRequest/{id}", method = RequestMethod.PUT)
    public @ResponseBody RejectDemandResponse rejectDemandRequest(@PathVariable("id") String id, @RequestBody RejectDemandRequest request) {
        return demandService.rejectDemandRequest(id, request);
    }
    
    @RequestMapping(value = "/GetDemandByIdRequest/{id}", method = RequestMethod.GET)
    public @ResponseBody Demand getDemandByIdRequest(@PathVariable("id") int id) {
        Demand demand = null;
        
        try {
            demand = demandService.getDemandByIdRequest(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return demand;
    }
    
    @RequestMapping(value = "/GetDemandBreakdownRequest/{id}", method = RequestMethod.GET)
    public @ResponseBody
    GetDemandBreakdownResponse getDemandBreakdownRequest(@PathVariable("id") int id) {
        GetDemandBreakdownResponse response = null;
        
        try {
            response = demandService.getDemandBreakdownRequest(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return response;
    }
}
