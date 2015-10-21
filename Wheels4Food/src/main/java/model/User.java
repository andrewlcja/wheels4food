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

    public User() {
    }

    public User(String username, String hashedPassword, String salt, String organizationName, String email, String address, String postalCode, String pocName, String pocNumber, String licenseNumber, String role) {
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

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPocName(String pocName) {
        this.pocName = pocName;
    }

    public void setPocNumber(String pocNumber) {
        this.pocNumber = pocNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
