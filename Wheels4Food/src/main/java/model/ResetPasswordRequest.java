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
public class ResetPasswordRequest {
    private String username;
    private String newPassword;
    private String confirmNewPassword;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String username, String newPassword, String confirmNewPassword) {
        this.username = username;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }
}
