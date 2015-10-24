/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.CreateDemandRequest;
import model.CreateDemandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    CreateDemandResponse createSupplyRequest(@RequestBody CreateDemandRequest request) {
        return demandService.createDemandRequest(request);
    }
}
