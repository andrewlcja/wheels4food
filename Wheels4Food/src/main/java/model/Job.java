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
    
    @Column(name = "monday")
    private boolean monday;
    
    @Column(name = "tuesday")
    private boolean tuesday;
    
    @Column(name = "wednesday")
    private boolean wednesday;
    
    @Column(name = "thursday")
    private boolean thursday;
    
    @Column(name = "friday")
    private boolean friday;
    
    @Column(name = "expiryDate")
    private String expiryDate;
    
    public Job() {        
    }

    public Job(Demand demand, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, String expiryDate) {
        this.demand = demand;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public Demand getDemand() {
        return demand;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
