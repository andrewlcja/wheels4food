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
    
    @Column(name = "schedule")
    private String schedule;
    
    @Column(name = "expiryDate")
    private String expiryDate;
    
    @Column(name = "status")
    private String status;
    
    public Job() {        
    }

    public Job(Demand demand, String schedule, String expiryDate, String status) {
        this.demand = demand;
        this.schedule = schedule;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Demand getDemand() {
        return demand;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
