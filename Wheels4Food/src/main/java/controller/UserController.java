/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.ActivateUserResponse;
import model.ChangePasswordRequest;
import model.ChangePasswordResponse;
import model.DeleteUserResponse;
import model.SuspendUserResponse;
import model.UpdateUserResponse;
import model.User;
import model.UserLoginRequest;
import model.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.UserService;

/**
 *
 * @author andrew.lim.2013
 */
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/GetUserListRequest", method = RequestMethod.GET)
    public @ResponseBody List<User> getUserListRequest() {

        List<User> userList = null;
        try {
            userList = userService.getUserListRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }
    
    @RequestMapping(value = "/GetUserListByRoleRequest/{role}", method = RequestMethod.GET)
    public @ResponseBody List<User> getUserListByRoleRequest(@PathVariable("role") String role) {

        List<User> userList = null;
        try {
            userList = userService.getUserListByRoleRequest(role);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }
    
    @RequestMapping(value = "/GetVolunteerListByOrganizationRequest/{organizationName}", method = RequestMethod.GET)
    public @ResponseBody List<User> getVolunteerListByOrganizationRequest(@PathVariable("organizationName") String organizationName) {

        List<User> userList = null;
        try {
            userList = userService.getVolunteerListByOrganizationRequest(organizationName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }
    
    @RequestMapping(value = "/GetUserByUsernameRequest/{username}", method = RequestMethod.GET)
    public @ResponseBody User getUserByUsernameRequest(@PathVariable("username") String username) {

        User user = null;
        try {
            user = userService.getUserByUsernameRequest(username);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    @RequestMapping(value = "/UserLoginRequest", method = RequestMethod.POST)
    public @ResponseBody UserLoginResponse userLoginRequest(@RequestBody UserLoginRequest request) {
        return userService.userLoginRequest(request);
    }
    
    @RequestMapping(value = "/DeleteUserRequest/{id}", method = RequestMethod.DELETE)
    public @ResponseBody DeleteUserResponse deleteUserRequest(@PathVariable("id") String id) {
        return userService.deleteUserRequest(id);
    }
    
    @RequestMapping(value = "/SuspendUserRequest/{id}", method = RequestMethod.PUT)
    public @ResponseBody SuspendUserResponse suspendUserRequest(@PathVariable("id") String id) {
        return userService.suspendUserRequest(id);
    }
    
    @RequestMapping(value = "/ActivateUserRequest/{id}", method = RequestMethod.PUT)
    public @ResponseBody ActivateUserResponse activateUserRequest(@PathVariable("id") String id) {
        return userService.activateUserRequest(id);
    }
    
    @RequestMapping(value = "/UpdateUserRequest", method = RequestMethod.PUT)
    public @ResponseBody UpdateUserResponse userLoginRequest(@RequestBody User user) {
        return userService.updateUserRequest(user);
    }
    
    @RequestMapping(value = "/ChangePasswordRequest", method = RequestMethod.PUT)
    public @ResponseBody ChangePasswordResponse changePasswordRequest(@RequestBody ChangePasswordRequest request ) {
        return userService.changePasswordRequest(request);
    }
}
