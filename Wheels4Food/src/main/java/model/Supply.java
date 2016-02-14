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
    
    @Column(name = "sku")
    private String sku;

    @Column(name = "itemName")
    private String itemName;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "unit")
    private String unit;

    @Column(name = "quantitySupplied")
    private int quantitySupplied;
        
    @Column(name = "minimum")
    private int minimum;
    
    @Column(name = "maximum")
    private int maximum;
    
    @Column(name = "initialMaximum")
    private int initialMaximum;

    @Column(name = "expiryDate")
    private String expiryDate;
    
    @Column(name = "monetaryValue")
    private float monetaryValue;

    @Column(name = "datePosted")
    private String datePosted;

    public Supply() {
    }

    public Supply(User user, String sku, String itemName, String category, String unit, int quantitySupplied, int minimum, int maximum, int initialMaximum, String expiryDate, float monetaryValue, String datePosted) {
        this.user = user;
        this.sku = sku;
        this.itemName = itemName;
        this.category = category;
        this.unit = unit;
        this.quantitySupplied = quantitySupplied;        
        this.minimum = minimum;
        this.maximum = maximum;
        this.initialMaximum = initialMaximum;
        this.expiryDate = expiryDate;
        this.monetaryValue = monetaryValue;
        this.datePosted = datePosted;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getSku() {
        return sku;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }
    
    public int getQuantitySupplied() {
        return quantitySupplied;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public int getInitialMaximum() {
        return initialMaximum;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public float getMonetaryValue() {
        return monetaryValue;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setQuantitySupplied(int quantitySupplied) {
        this.quantitySupplied = quantitySupplied;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public void setInitialMaximum(int initialMaximum) {
        this.initialMaximum = initialMaximum;
    }
}
