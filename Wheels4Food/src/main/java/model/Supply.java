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
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author Wayne
 */
@Entity
@Table(name = "supply")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Supply {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "organizationName")
    private String organizationName;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "expiryDate")
    private String expiryDate;

    @Column(name = "supplyDatePosted")
    private String supplyDatePosted;

    @Column(name = "category")
    private String category;

    public Supply() {
    }

    public Supply(String username, String organizationName, String itemName, String quantity, String expiryDate, String supplyDatePosted, String category) {
        this.username = username;
        this.organizationName = organizationName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.supplyDatePosted = supplyDatePosted;
        this.category = category;
    }

    public String getUsername() {
        return username;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getSupplyDatePosted() {
        return supplyDatePosted;
    }

    public String getCategory() {
        return category;
    }

}
