/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author andrew.lim.2013
 */
@Entity
@Table(name = "demanditem")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DemandItem implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "demandID")
    private Demand demand;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "supplyID")
    private Supply supply;
    
    @Column(name = "quantityDemanded")
    private int quantityDemanded;

    public DemandItem() {
    }

    public DemandItem(Demand demand, Supply supply, int quantityDemanded) {
        this.demand = demand;
        this.supply = supply;
        this.quantityDemanded = quantityDemanded;
    }

    public int getId() {
        return id;
    }

    public Demand getDemand() {
        return demand;
    }

    public Supply getSupply() {
        return supply;
    }

    public int getQuantityDemanded() {
        return quantityDemanded;
    }
}
