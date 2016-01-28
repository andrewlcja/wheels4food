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
@Table(name = "selfcollection")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SelfCollection implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "demandID")
    private Demand demand;
    
    @Column(name = "deliveryDate")
    private String deliveryDate;
    
    @Column(name = "timeslot")
    private String timeslot;
    
    @Column(name = "status")
    private String status;
    
    public SelfCollection() {        
    }

    public SelfCollection(Demand demand, String deliveryDate, String timeslot, String status) {
        this.demand = demand;
        this.deliveryDate = deliveryDate;
        this.timeslot = timeslot;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Demand getDemand() {
        return demand;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
