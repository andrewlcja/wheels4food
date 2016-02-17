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
@Table(name = "job")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Job implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "demandID")
    private Demand demand;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "userID")
    private User user;
    
    @Column(name = "deliveryDate")
    private String deliveryDate;
    
    @Column(name = "timeslot")
    private String timeslot;
    
    @Column(name = "expiryDate")
    private String expiryDate;
    
    @Column(name = "status")
    private String status; 
        
    @Column(name = "collectionTime")
    private String collectionTime;
    
    @Column(name = "deliveryTime")
    private String deliveryTime;
    
    public Job() {        
    }

    public Job(Demand demand, User user, String deliveryDate, String timeslot, String expiryDate, String status, String collectionTime, String deliveryTime) {
        this.demand = demand;
        this.user = user;
        this.deliveryDate = deliveryDate;
        this.timeslot = timeslot;
        this.expiryDate = expiryDate;
        this.status = status;
        this.collectionTime = collectionTime;
        this.deliveryTime = deliveryTime;
    }

    public int getId() {
        return id;
    }

    public Demand getDemand() {
        return demand;
    }

    public User getUser() {
        return user;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCollectionTime() {
        return collectionTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }
   
    public void setStatus(String status) {
        this.status = status;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setCollectionTime(String collectionTime) {
        this.collectionTime = collectionTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
