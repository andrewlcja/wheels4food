/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ItemService;

/**
 *
 * @author andrew.lim.2013
 */
@Controller
public class ItemController {

    @Autowired
    ItemService itemService;
    
    @RequestMapping(value = "/GetItemListRequest", method = RequestMethod.GET)
    public @ResponseBody
    List<Item> getItemListRequest() {
        List<Item> itemList = null;

        try {
            itemList = itemService.getItemListRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemList;
    }
}
