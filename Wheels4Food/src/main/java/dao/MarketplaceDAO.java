/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import model.PendingRegistration;
import model.Supply;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
}
