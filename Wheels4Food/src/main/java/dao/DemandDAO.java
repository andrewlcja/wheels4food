/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.Demand;
import model.DemandItem;
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

    public Demand createDemand(Demand demand) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(demand);
        tx.commit();
        session.close();
        return demand;
    }
    
    public void createDemandItem(DemandItem demandItem) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(demandItem);
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
    
    public void updateDemandItem(DemandItem demandItem) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(demandItem);
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
    
    public void deleteDemandItem(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        DemandItem demandItem = (DemandItem) session.load(DemandItem.class, id);
        session.delete(demandItem);
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
    
    public List<DemandItem> getDemandItemList() throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<DemandItem> demandItemList = session.createCriteria(DemandItem.class).list();
        tx.commit();
        session.close();
        return demandItemList;
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
    
    public List<DemandItem> getDemandItemListByDemandId(int demandID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<DemandItem> demandItemList = session.createCriteria(DemandItem.class)
                .add(Restrictions.eq("demand.id", demandID)).list();
        tx.commit();
        session.close();
        return demandItemList;
    }
    
    public List<DemandItem> getDemandItemListByRequesterId(int requesterID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<DemandItem> demandItemList = session.createCriteria(DemandItem.class)
                .createAlias("demand.user", "requester")
                .add(Restrictions.eq("requester.id", requesterID)).list();
        tx.commit();
        session.close();
        return demandItemList;
    }
    
    public List<DemandItem> getDemandItemListBySupplierId(int supplierID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<DemandItem> demandItemList = session.createCriteria(DemandItem.class)
                .createAlias("demand.supplier", "supplier")
                .add(Restrictions.eq("supplier.id", supplierID)).list();
        tx.commit();
        session.close();
        return demandItemList;
    }

    public List<Demand> getDemandListByDeliveryDate(int supplierID, String deliveryDate) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Demand> demandList = session.createCriteria(Demand.class)
                .add(Restrictions.eq("supplier.id", supplierID))
                .add(Restrictions.eq("preferredDeliveryDate", deliveryDate))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("status", "Pending"))
                        .add(Restrictions.eq("status", "Self Collection Created"))
                        .add(Restrictions.eq("status", "Approved"))).list();
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
                .add(Restrictions.eq("supplier.id", supplierID))
                .add(Restrictions.eq("status", "Pending")).list();
        tx.commit();
        session.close();
        return demandList;
    }
    
    public List<Demand> getCompletedDemandListBySupplierId(int supplierID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Demand> demandList = session.createCriteria(Demand.class)
                .add(Restrictions.eq("supplier.id", supplierID))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("status", "Job Completed"))
                        .add(Restrictions.eq("status", "Self Collection Completed"))).list();
        tx.commit();
        session.close();
        return demandList;
    }
    
    public List<Demand> getApprovedDemandListBySupplierId(int supplierID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Demand> demandList = session.createCriteria(Demand.class)
                .add(Restrictions.eq("supplier.id", supplierID))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("status", "Job Created"))
                        .add(Restrictions.eq("status", "Job Accepted"))
                        .add(Restrictions.eq("status", "Job Completed"))
                        .add(Restrictions.eq("status", "Job Cancelled"))
                        .add(Restrictions.eq("status", "Self Collection Created"))
                        .add(Restrictions.eq("status", "Self Collection Completed"))
                        .add(Restrictions.eq("status", "Self Collection Cancelled"))).list();
        tx.commit();
        session.close();
        return demandList;
    }
    
    public List<Demand> getDemandListBySupplierOrRequesterId(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Demand> demandList = session.createCriteria(Demand.class)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("supplier.id", id))
                        .add(Restrictions.eq("user.id", id))).list();
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
