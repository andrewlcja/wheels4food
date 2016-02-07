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
@Table(name = "pendingregistration")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PendingRegistration implements Serializable {
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
    
    @Column(name = "description")
    private String description;

    @Column(name = "role")
    private String role;

    public PendingRegistration() {
    }

    public PendingRegistration(String username, String hashedPassword, String salt, String organizationName, String email, String address, String postalCode, String pocName, String pocNumber, String licenseNumber, String description, String role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.organizationName = organizationName;
        this.email = email;
        this.address = address;
        this.postalCode = postalCode;
        this.pocName = pocName;
        this.pocNumber = pocNumber;
        this.licenseNumber = licenseNumber;
        this.description = description;
        this.role = role;
    }

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

    public String getDescription() {
        return description;
    }

    public String getRole() {
        return role;
    }
}
