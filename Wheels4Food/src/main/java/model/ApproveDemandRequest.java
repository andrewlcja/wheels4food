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
public class ApproveDemandRequest {

    String comments;

    public ApproveDemandRequest() {

    }

    public ApproveDemandRequest(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }
}
