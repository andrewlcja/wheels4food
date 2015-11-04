/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.Job;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class JobDAO {

    @Autowired
    SessionFactory sessionFactory;

    Session session = null;
    Transaction tx = null;
    
    public void createJob(Job job) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(job);
        tx.commit();
        session.close();
    }
    
    public void updateJob(Job job) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(job);
        tx.commit();
        session.close();
    }
    
    public List<Job> retrieveAll() throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Job> jobList = session.createCriteria(Job.class).list();
        tx.commit();
        session.close();
        return jobList;
    }
}
