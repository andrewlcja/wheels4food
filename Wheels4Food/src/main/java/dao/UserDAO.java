/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.PendingResetPassword;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class UserDAO {

    @Autowired
    SessionFactory sessionFactory;

    Session session = null;
    Transaction tx = null;

    public List<User> retrieveAll() throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<User> userList = session.createCriteria(User.class).list();
        tx.commit();
        session.close();
        return userList;
    }

    public List<User> getUserListByRole(String role) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();

        List<User> userList = null;

        if (role.equals("VWO")) {
            userList = session.createCriteria(User.class)
                    .addOrder(Order.asc("organizationName"))
                    .add(Restrictions.ne("username", "ffth"))
                    .add(Restrictions.disjunction()
                            .add(Restrictions.eq("role", "Supplier"))
                            .add(Restrictions.eq("role", "Requester"))).list();
        } else {
            userList = session.createCriteria(User.class)
                    .add(Restrictions.eq("role", role))
                    .addOrder(Order.asc("organizationName")).list();
        }

        tx.commit();
        session.close();
        return userList;
    }

    public List<User> getVolunteerListByOrganization(String organizationName) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<User> userList = session.createCriteria(User.class)
                .add(Restrictions.eq("role", "Volunteer"))
                .add(Restrictions.eq("organizationName", organizationName)).list();
        tx.commit();
        session.close();
        return userList;
    }

    public User getUser(String username) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        User user = (User) session.createCriteria(User.class)
                .add(Restrictions.eq("username", username))
                .uniqueResult();
        tx.commit();
        session.close();
        return user;
    }

    public User getUserByOrganization(String organization) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        User user = (User) session.createCriteria(User.class)
                .add(Restrictions.eq("organizationName", organization))
                .add(Restrictions.ne("role", "Volunteer"))
                .uniqueResult();
        tx.commit();
        session.close();
        return user;
    }

    public User getUserById(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        User user = (User) session.createCriteria(User.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        tx.commit();
        session.close();
        return user;
    }

    public User getUserByEmail(String email) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        User user = (User) session.createCriteria(User.class)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
        tx.commit();
        session.close();
        return user;
    }

    public User getUserByMobileNumber(String number) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        User user = (User) session.createCriteria(User.class)
                .add(Restrictions.eq("pocNumber", number))
                .add(Restrictions.eq("role", "Volunteer"))
                .uniqueResult();
        tx.commit();
        session.close();
        return user;
    }

    public void deleteUser(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        User user = (User) session.load(User.class, id);
        session.delete(user);
        tx.commit();
        session.close();
    }

    public void updateUser(User user) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(user);
        tx.commit();
        session.close();
    }

    public void createUser(User user) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(user);
        tx.commit();
        session.close();
    }
    
    public void createPendingResetPassword(PendingResetPassword pendingResetPassword) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(pendingResetPassword);
        tx.commit();
        session.close();
    }
    
    public List<PendingResetPassword> getPendingResetPasswordList() throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<PendingResetPassword> pendingResetPasswordList = session.createCriteria(PendingResetPassword.class).list();
        tx.commit();
        session.close();
        return pendingResetPasswordList;
    }
    
    public PendingResetPassword getPendingResetPassword(String username) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        PendingResetPassword pendingResetPassword = (PendingResetPassword) session.createCriteria(PendingResetPassword.class)
                .add(Restrictions.eq("username", username))
                .uniqueResult();
        tx.commit();
        session.close();
        return pendingResetPassword;
    }
    
    public void updatePendingResetPassword(PendingResetPassword pendingResetPassword) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(pendingResetPassword);
        tx.commit();
        session.close();
    }
    
    public void deletePendingResetPassword(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        PendingResetPassword pendingResetPassword = (PendingResetPassword) session.load(PendingResetPassword.class, id);
        session.delete(pendingResetPassword);
        tx.commit();
        session.close();
    }
}
