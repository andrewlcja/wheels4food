/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author andrew.lim.2013
 */
public class CreatePendingRegistrationRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String organizationName;
    private String email;
    private String address;
    private String postalCode;
    private String pocName;
    private String pocNumber;
    private String licenseNumber;
    private String role;

    public CreatePendingRegistrationRequest() {
    }

    public CreatePendingRegistrationRequest(String username, String password, String confirmPassword, String organizationName, String email, String address, String postalCode, String pocName, String pocNumber, String licenseNumber, String role) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.organizationName = organizationName;
        this.email = email;
        this.address = address;
        this.postalCode = postalCode;
        this.pocName = pocName;
        this.pocNumber = pocNumber;
        this.licenseNumber = licenseNumber;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
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
