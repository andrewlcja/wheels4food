/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import javax.persistence.Query;
import model.Supply;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Wayne
 */
public class SupplyDAO {

    @Autowired
    SessionFactory sessionFactory;

    Session session = null;
    Transaction tx = null;

    public void createSupply(Supply supply) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(supply);
        tx.commit();
        session.close();
    }

    public void deleteSupply(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Supply supply = (Supply) session.load(Supply.class, id);
        session.delete(supply);
        tx.commit();
        session.close();
    }

    public void updateSupply(Supply supply) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(supply);
        tx.commit();
        session.close();
    }

    public List<Supply> retrieveAll() throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Supply> supplyList = session.createCriteria(Supply.class)
                .createAlias("user", "supplier")
                .add(Restrictions.ne("quantitySupplied", 0))
                .add(Restrictions.eq("supplier.role", "Supplier"))
                .addOrder(Order.asc("itemName")).list();
        tx.commit();
        session.close();
        return supplyList;
    }

    public List<Supply> getSupplyListByUserId(int userID) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Supply> supplyList = session.createCriteria(Supply.class)
                .add(Restrictions.eq("user.id", userID)).list();
        tx.commit();
        session.close();
        return supplyList;
    }
    
    public List<Supply> getSupplyListByCategory(String category) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Supply> supplyList = session.createCriteria(Supply.class)
                .createAlias("user", "supplier")
                .add(Restrictions.eq("category", category))
                .add(Restrictions.ne("quantitySupplied", 0))
                .add(Restrictions.eq("supplier.role", "Supplier"))
                .addOrder(Order.asc("itemName")).list();
        tx.commit();
        session.close();
        return supplyList;
    }

    public Supply getSupplyById(int id) {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Supply supply = (Supply) session.createCriteria(Supply.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        tx.commit();
        session.close();
        return supply;
    }
}
