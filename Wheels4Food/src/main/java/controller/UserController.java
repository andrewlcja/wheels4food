/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.User;
import model.UserLoginRequest;
import model.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(value = "/getUserListService", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody
    List<User> getUserListService() {

        List<User> userList = null;
        try {
            userList = userService.getEntityList();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    @RequestMapping(value = "/UserLoginRequest", method = RequestMethod.POST)
    public @ResponseBody UserLoginResponse userLoginRequest(@RequestBody UserLoginRequest request) {
        return userService.userLoginRequest(request);
    }
}
