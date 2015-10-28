/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

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
 * @author Wayne
 */
@Entity
@Table(name = "demand")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Demand {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userID")
    private User user;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "supplyID")
    private Supply supply;
    
    @Column(name = "quantityDemanded")
    private int quantityDemanded;
    
    @Column(name = "status")
    private String status;

    public Demand() {
    }

    public Demand(User user, Supply supply, int quantityDemanded, String status) {
        this.user = user;
        this.supply = supply;
        this.quantityDemanded = quantityDemanded;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Supply getSupply() {
        return supply;
    }

    public int getQuantityDemanded() {
        return quantityDemanded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
