/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class ItemDAO {

    @Autowired
    SessionFactory sessionFactory;

    Session session = null;
    Transaction tx = null;
    
    public Item getItemById(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Item item = (Item) session.createCriteria(Item.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        tx.commit();
        session.close();
        return item;
    }
    
    public void createItem(Item item) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.save(item);
        tx.commit();
        session.close();
    }
    
    public void deleteItem(int id) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Item item = (Item) session.load(Item.class, id);
        session.delete(item);
        tx.commit();
        session.close();
    }
    
    public void updateItem(Item item) throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        session.update(item);
        tx.commit();
        session.close();
    }
    
    public List<Item> retrieveAll() throws Exception {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        List<Item> itemList = session.createCriteria(Item.class).list();
        tx.commit();
        session.close();
        return itemList;
    }
}
