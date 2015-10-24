/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.ItemDAO;
import java.util.List;
import model.Item;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andrew.lim.2013
 */
public class ItemService {

    @Autowired
    ItemDAO itemDAO;
    
    public List<Item> getItemListRequest() throws Exception {
        return itemDAO.retrieveAll();
    }
}
