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
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author andrew.lim.2013
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "hashedPassword")
    private String hashedPassword;

    @Column(name = "salt")
    private String salt;
    
    @Column(name = "organizationName")
    private String organizationName;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "postalCode")
    private String postalCode;
    
    @Column(name = "pocName")
    private String pocName;
    
    @Column(name = "pocNumber")
    private String pocNumber;
    
    @Column(name = "licenseNumber")
    private String licenseNumber;
    
    @Column(name = "role")
    private String role;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPocName() {
        return pocName;
    }

    public String getPocNumber() {
        return pocNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getRole() {
        return role;
    }
}
