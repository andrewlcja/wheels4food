/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.SelfCollection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class SelfCollectionDAO {
    @Autowired
    SessionFactory sessionFactory;

    Session session = null;
    Transaction tx = null;
    
    public void createSelfCollection(SelfCollection selfCollection) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(selfCollection);
        tx.commit();
        session.close();
    }
    
    public void updateSelfCollection(SelfCollection selfCollection) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(selfCollection);
        tx.commit();
        session.close();
    }
    
    public SelfCollection getSelfCollectionById(int id) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        SelfCollection selfCollection = (SelfCollection) session.createCriteria(SelfCollection.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        tx.commit();
        session.close();
        return selfCollection;
    }
    
    public SelfCollection getSelfCollectionByDemandId(int demandID) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        SelfCollection selfCollection = (SelfCollection) session.createCriteria(SelfCollection.class)
                .add(Restrictions.eq("demand.id", demandID))
                .uniqueResult();
        tx.commit();
        session.close();
        return selfCollection;
    }
    
    public List<SelfCollection> retrieveAll() throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<SelfCollection> jobList = session.createCriteria(SelfCollection.class)
                .add(Restrictions.eq("status", "Active"))
                .list();
        tx.commit();
        session.close();
        return jobList;
    }
    
    public List<SelfCollection> getSelfCollectionListBySupplierId(int supplierID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<SelfCollection> selfCollectionList = session.createCriteria(SelfCollection.class)
                .createAlias("demand.supplier", "supplier")
                .createAlias("demand.user", "requester")
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("supplier.id", supplierID))
                        .add(Restrictions.eq("requester.id", supplierID))).list();
        tx.commit();
        session.close();
        return selfCollectionList;
    }
}
