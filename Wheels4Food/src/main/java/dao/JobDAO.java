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
import org.hibernate.criterion.Restrictions;
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
        List<Job> jobList = session.createCriteria(Job.class)
                .add(Restrictions.eq("status", "Active"))
                .list();
        tx.commit();
        session.close();
        return jobList;
    }
    
    public Job getJobByDemandId(int demandID) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Job job = (Job) session.createCriteria(Job.class)
                .add(Restrictions.eq("demand.id", demandID))
                .uniqueResult();
        tx.commit();
        session.close();
        return job;
    }
    
    public Job getJobById(int id) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Job job = (Job) session.createCriteria(Job.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        tx.commit();
        session.close();
        return job;
    }
}
