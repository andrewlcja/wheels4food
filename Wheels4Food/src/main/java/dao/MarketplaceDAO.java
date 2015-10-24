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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Wayne
 */
public class MarketplaceDAO {

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
  public List<Supply> retrieveAll() {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Supply> supplyList = session.createCriteria(Supply.class).list();
        tx.commit();
        session.close();
        return supplyList;
    }
  
  public List<Supply> getSupplyListByUsername(String username){
       session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Supply> supplyList = session.createCriteria(Supply.class)
                .add(Restrictions.eq("username", username)).list();
        tx.commit();
        session.close();
        return supplyList;
  }

}
