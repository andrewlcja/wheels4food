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
public class UserLogoutResponse {
    private boolean isLoggedOut;

    public UserLogoutResponse(boolean isLoggedOut) {
        this.isLoggedOut = isLoggedOut;
    }

    public boolean isIsLoggedOut() {
        return isLoggedOut;
    }
}
