/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.DeleteNotificationResponse;
import model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.NotificationService;

/**
 *
 * @author andrew.lim.2013
 */
@Controller
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @RequestMapping(value = "/GetNotificationListByUserIdRequest/{userID}", method = RequestMethod.GET)
    public @ResponseBody
    List<Notification> getNotificationListByUserIdRequest(@PathVariable("userID") int userID) {
        List<Notification> notificationList = null;

        try {
            notificationList = notificationService.getNotificationListByUserIdRequest(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notificationList;
    }

    @RequestMapping(value = "/DeleteNotificationRequest/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    DeleteNotificationResponse deleteNotificationRequest(@PathVariable("id") String id) {
        return notificationService.deleteNotificationRequest(id);
    }
}
