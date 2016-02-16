/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;

/**
 *
 * @author andrew.lim.2013
 */
public class UpdateDemandRequest {
    private Demand demand;
    private List<DemandItem> demandItemList;

    public UpdateDemandRequest() {
    }

    public UpdateDemandRequest(Demand demand, List<DemandItem> demandItemList) {
        this.demand = demand;
        this.demandItemList = demandItemList;
    }

    public Demand getDemand() {
        return demand;
    }

    public List<DemandItem> getDemandItemList() {
        return demandItemList;
    }
}
