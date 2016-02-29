/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.Notification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class NotificationDAO {

    @Autowired
    SessionFactory sessionFactory;

    Session session = null;
    Transaction tx = null;
    
    public void createNotification(Notification notification) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(notification);
        tx.commit();
        session.close();
    }
    
    public void updateNotification(Notification notification) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(notification);
        tx.commit();
        session.close();
    }
    
    public List<Notification> getNotificationListByUserId(int userID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Notification> notificationList = session.createCriteria(Notification.class)
                .add(Restrictions.eq("user.id", userID))
                .list();
        tx.commit();
        session.close();
        return notificationList;
    }
    
    public void deleteNotification(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Notification notification = (Notification) session.load(Notification.class, id);
        session.delete(notification);
        tx.commit();
        session.close();
    }
}
