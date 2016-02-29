/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import model.PendingRegistration;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class PendingRegistrationDAO {

    @Autowired
    SessionFactory sessionFactory;

    Session session = null;
    Transaction tx = null;

    public void createPendingRegistration(PendingRegistration pendingRegistration) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(pendingRegistration);
        tx.commit();
        session.close();
    }

    public void deletePendingRegistration(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        PendingRegistration pendingRegistration = (PendingRegistration) session.load(PendingRegistration.class, id);
        session.delete(pendingRegistration);
        tx.commit();
        session.close();
    }

    public PendingRegistration getPendingRegistrationByUsername(String username) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        PendingRegistration pendingRegistration = (PendingRegistration) session.createCriteria(PendingRegistration.class)
                .add(Restrictions.eq("username", username))
                .uniqueResult();
        tx.commit();
        session.close();
        return pendingRegistration;
    }
    
    public PendingRegistration getPendingRegistrationByOrganization(String organization) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        PendingRegistration pendingRegistration = (PendingRegistration) session.createCriteria(PendingRegistration.class)
                .add(Restrictions.eq("organizationName", organization))
                .add(Restrictions.eq("role", "VWO"))
                .uniqueResult();
        tx.commit();
        session.close();
        return pendingRegistration;
    }
    
    public PendingRegistration getPendingRegistrationById(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        PendingRegistration pendingRegistration = (PendingRegistration) session.createCriteria(PendingRegistration.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        tx.commit();
        session.close();
        return pendingRegistration;
    }

    public List<PendingRegistration> retrieveAll() {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<PendingRegistration> pendingRegistrationList = session.createCriteria(PendingRegistration.class).list();
        tx.commit();
        session.close();
        return pendingRegistrationList;
    }
    
    public List<PendingRegistration> getPendingRegistrationListByRole(String role) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<PendingRegistration> pendingRegistrationList = session.createCriteria(PendingRegistration.class)
                .add(Restrictions.eq("role", role)).list();
        tx.commit();
        session.close();
        return pendingRegistrationList;
    }
    
    public List<PendingRegistration> getVolunteerPendingRegistrationListByOrganization(String organizationName) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<PendingRegistration> pendingRegistrationList = session.createCriteria(PendingRegistration.class)
                .add(Restrictions.eq("organizationName", organizationName))
                .add(Restrictions.eq("role", "Volunteer")).list();
        tx.commit();
        session.close();
        return pendingRegistrationList;
    }
    
    public List<PendingRegistration> getPendingRegistrationListByOrganization(String organizationName) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<PendingRegistration> pendingRegistrationList = session.createCriteria(PendingRegistration.class)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("organizationName", organizationName))
                        .add(Restrictions.eq("role", "Requester"))).list();
        tx.commit();
        session.close();
        return pendingRegistrationList;
    }
    
    public void approvePendingRegistration(User user) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();        
        session.save(user);
        tx.commit();
        session.close();
    }
}
