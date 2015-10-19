/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author andrew.lim.2013
 */
public class UserLoginResponse {
    private boolean isAuthenicated;
    private User user;
    private String error;

    public UserLoginResponse(boolean isAuthenicated, String error) {
        this.isAuthenicated = isAuthenicated;
        this.error = error;
    }
    
    public UserLoginResponse(boolean isAuthenicated, User user) {
        this.isAuthenicated = isAuthenicated;
        this.user = user;
    }

    public boolean isIsAuthenicated() {
        return isAuthenicated;
    }
    
    public User getUser() {
        return user;
    }

    public String getError() {
        return error;
    }
}
