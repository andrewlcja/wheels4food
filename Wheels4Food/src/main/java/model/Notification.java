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
@Table(name = "notification")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "userID")
    private User user;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "message")
    private String message;

    public Notification() {
    }

    public Notification(User user, String state, String message) {
        this.user = user;
        this.state = state;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }
}