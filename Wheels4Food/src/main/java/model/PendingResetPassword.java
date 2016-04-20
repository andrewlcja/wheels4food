/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Timestamp;
import java.util.Date;
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
@Table(name = "pendingresetpassword")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PendingResetPassword {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    
    @Column(name = "username")
    private String username;

    @Column(name = "hashedToken")
    private String hashedToken;

    @Column(name = "salt")
    private String salt;

    @Column(name = "expiryDate")
    private Timestamp expiryDate;

    public PendingResetPassword() {
    }

    public PendingResetPassword(String username, String hashedToken, String salt, Timestamp expiryDate) {
        this.username = username;
        this.hashedToken = hashedToken;
        this.salt = salt;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }

    public String getHashedToken() {
        return hashedToken;
    }

    public String getSalt() {
        return salt;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHashedToken(String hashedToken) {
        this.hashedToken = hashedToken;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

}
