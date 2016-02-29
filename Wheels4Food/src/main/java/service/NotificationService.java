/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.NotificationDAO;
import java.util.ArrayList;
import java.util.List;
import model.DeleteNotificationResponse;
import model.Notification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class NotificationService {

    @Autowired
    NotificationDAO notificationDAO;
    
    public List<Notification> getNotificationListByUserIdRequest(int userID) throws Exception {
        return notificationDAO.getNotificationListByUserId(userID);
    }
    
    public DeleteNotificationResponse deleteNotificationRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new DeleteNotificationResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                notificationDAO.deleteNotification(id);
                return new DeleteNotificationResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new DeleteNotificationResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new DeleteNotificationResponse(false, errorList);
        }
    }
}
