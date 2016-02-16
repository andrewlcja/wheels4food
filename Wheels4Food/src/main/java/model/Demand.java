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
 * @author Wayne
 */
@Entity
@Table(name = "demand")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Demand implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userID")
    private User user;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "supplierID")
    private User supplier;
        
    @Column(name = "dateRequested")
    private String dateRequested;
    
    @Column(name = "preferredDeliveryDate")
    private String preferredDeliveryDate;
    
    @Column(name = "preferredTimeslot")
    private String preferredTimeslot;
    
    @Column(name = "preferredSchedule")
    private String preferredSchedule;    
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "comments")
    private String comments;

    public Demand() {
    }

    public Demand(User user, User supplier, String dateRequested, String preferredDeliveryDate, String preferredTimeslot, String preferredSchedule, String status, String comments) {
        this.user = user;
        this.supplier = supplier;
        this.dateRequested = dateRequested;
        this.preferredDeliveryDate = preferredDeliveryDate;
        this.preferredTimeslot = preferredTimeslot;
        this.preferredSchedule = preferredSchedule;
        this.status = status;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public User getSupplier() {
        return supplier;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public String getPreferredDeliveryDate() {
        return preferredDeliveryDate;
    }

    public String getPreferredTimeslot() {
        return preferredTimeslot;
    }

    public String getPreferredSchedule() {
        return preferredSchedule;
    }

    public String getStatus() {
        return status;
    }

    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
