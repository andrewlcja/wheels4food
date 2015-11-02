/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.Demand;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class DemandDAO {
    @Autowired
    SessionFactory sessionFactory;

    Session session = null;
    Transaction tx = null;
    
    public void createDemand(Demand demand) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(demand);
        tx.commit();
        session.close();
    }
    
    public void updateDemand(Demand demand) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(demand);
        tx.commit();
        session.close();
    }
    
    public void deleteDemand(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Demand demand = (Demand) session.load(Demand.class, id);
        session.delete(demand);
        tx.commit();
        session.close();
    }
    
    public List<Demand> retrieveAll() throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Demand> demandList = session.createCriteria(Demand.class).list();
        tx.commit();
        session.close();
        return demandList;
    }
    
    public List<Demand> getDemandListByUserId(int userID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Demand> demandList = session.createCriteria(Demand.class)
                .add(Restrictions.eq("user.id", userID)).list();
        tx.commit();
        session.close();
        return demandList;
    }
    
    public List<Demand> getDemandListBySupplyId(int supplyID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Demand> demandList = session.createCriteria(Demand.class)
                .add(Restrictions.eq("supply.id", supplyID)).list();
        tx.commit();
        session.close();
        return demandList;
    }
    
    public List<Demand> getPendingDemandListBySupplierId(int supplierID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Demand> demandList = session.createCriteria(Demand.class)
                .createAlias("supply.user", "supplier")
                .add(Restrictions.eq("supplier.id", supplierID))
                .add(Restrictions.eq("status", "Pending")).list();
        tx.commit();
        session.close();
        return demandList;
    }
    
    public Demand getDemandById(int id) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Demand demand = (Demand) session.createCriteria(Demand.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        tx.commit();
        session.close();
        return demand;
    }
}
