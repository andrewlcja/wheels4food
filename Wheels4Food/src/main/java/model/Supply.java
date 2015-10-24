/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.CascadeType;
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
@Table(name = "supply")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Supply implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "itemID")
    private Item item;

    @Column(name = "quantitySupplied")
    private int quantitySupplied;

    @Column(name = "expiryDate")
    private String expiryDate;

    @Column(name = "datePosted")
    private String datePosted;

    public Supply() {
    }

    public Supply(User user, Item item, int quantitySupplied, String expiryDate, String datePosted) {
        this.user = user;
        this.item = item;
        this.quantitySupplied = quantitySupplied;
        this.expiryDate = expiryDate;
        this.datePosted = datePosted;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantitySupplied() {
        return quantitySupplied;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setQuantitySupplied(int quantitySupplied) {
        this.quantitySupplied = quantitySupplied;
    }
}
