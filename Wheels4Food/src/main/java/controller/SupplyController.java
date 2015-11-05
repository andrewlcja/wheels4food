/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.CreateSupplyRequest;
import model.CreateSupplyResponse;
import model.DeleteSupplyResponse;
import model.Supply;
import model.UpdateSupplyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import service.SupplyService;

/**
 *
 * @author Wayne
 */
@Controller
public class SupplyController {

    @Autowired
    SupplyService supplyService;

    @RequestMapping(value = "/CreateSupplyRequest", method = RequestMethod.POST)
    public @ResponseBody
    CreateSupplyResponse createSupplyRequest(@RequestBody CreateSupplyRequest request) {
        return supplyService.createSupplyRequest(request);
    }

    @RequestMapping(value = "/UpdateSupplyRequest", method = RequestMethod.PUT)
    public @ResponseBody
    UpdateSupplyResponse updateSupplyRequest(@RequestBody Supply supply) {
        return supplyService.updateSupplyRequest(supply);
    }

    @RequestMapping(value = "/DeleteSupplyRequest/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    DeleteSupplyResponse deleteSupplyRequest(@PathVariable("id") String id) {
        return supplyService.deleteSupplyRequest(id);
    }

    @RequestMapping(value = "/GetSupplyListRequest", method = RequestMethod.GET)
    public @ResponseBody
    List<Supply> getSupplyListRequest() {
        List<Supply> supplyList = null;

        try {
            supplyList = supplyService.getSupplyListRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return supplyList;
    }

    @RequestMapping(value = "/GetSupplyListByUserIdRequest/{userID}", method = RequestMethod.GET)
    public @ResponseBody
    List<Supply> getSupplyListByUserIdRequest(@PathVariable("userID") int userID) {
        List<Supply> supplyList = null;

        try {
            supplyList = supplyService.getSupplyListByUserIdRequest(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return supplyList;
    }
    
    @RequestMapping(value = "/GetSupplyListByCategoryRequest/{category}", method = RequestMethod.GET)
    public @ResponseBody
    List<Supply> getSupplyListByCategoryRequest(@PathVariable("category") String category) {
        List<Supply> supplyList = null;

        try {
            supplyList = supplyService.getSupplyListByCategoryRequest(category);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return supplyList;
    }

    @RequestMapping(value = "/GetSupplyByIdRequest/{id}", method = RequestMethod.GET)
    public @ResponseBody Supply getSupplyByIdRequest(@PathVariable("id") int id) {
        Supply supply = null;
        
        try {
            supply = supplyService.getSupplyByIdRequest(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return supply;
    }
}
