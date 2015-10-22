/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.ApprovePendingRegistrationResponse;
import model.CreateMarketplaceSupplyRequest;
import model.CreateMarketplaceSupplyResponse;
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
import org.springframework.stereotype.Controller;
import service.MarketplaceService;

/**
 *
 * @author Wayne
 */
@Controller
public class MarketplaceController {
    @Autowired
    MarketplaceService marketplaceService;
    
    @RequestMapping(value = "/CreateMarketplaceSupplyRequest", method = RequestMethod.POST)
    public @ResponseBody CreateMarketplaceSupplyResponse CreateMarketplaceSupplyRequest(@RequestBody CreateMarketplaceSupplyRequest request) {
        return marketplaceService.CreateMarketplaceSupplyRequest(request);
    }
}
