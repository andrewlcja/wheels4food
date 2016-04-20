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
public class CreatePendingResetPasswordRequest {
    private String email;
    private String endPoint;

    public CreatePendingResetPasswordRequest() {
    }

    public CreatePendingResetPasswordRequest(String email, String endPoint) {
        this.email = email;
        this.endPoint = endPoint;
    }

    public String getEmail() {
        return email;
    }

    public String getEndPoint() {
        return endPoint;
    }
}
