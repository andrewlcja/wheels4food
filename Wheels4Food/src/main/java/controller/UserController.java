/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.DeleteUserResponse;
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

    @RequestMapping(value = "/UserLoginRequest", method = RequestMethod.POST)
    public @ResponseBody UserLoginResponse userLoginRequest(@RequestBody UserLoginRequest request) {
        return userService.userLoginRequest(request);
    }
    
    @RequestMapping(value = "/DeleteUserRequest/{id}", method = RequestMethod.DELETE)
    public @ResponseBody DeleteUserResponse deleteUserRequest(@PathVariable("id") String id) {
        return userService.deleteUserRequest(id);
    }
    
    @RequestMapping(value = "/UpdateUserRequest", method = RequestMethod.PUT)
    public @ResponseBody UpdateUserResponse userLoginRequest(@RequestBody User user) {
        return userService.updateUserRequest(user);
    }
}
